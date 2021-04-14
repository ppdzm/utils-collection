package io.github.ppdzm.utils.hadoop.hbase

import java.util

import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.snapshot.{SnapshotCreationException, SnapshotExistsException}
import org.apache.hadoop.hbase.util.Bytes
import org.sa.utils.universal.base.{DateTimeUtils, Logging}
import org.sa.utils.universal.implicits.BasicConversions._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Created by Stuart Alex on 2017/1/11.
 */
case class HBaseHandler(zookeeperQuorum: String, zookeeperPort: Int) extends HBaseEnvironment with Logging {

    def bulkDelete(table: String, keyRegexp: String, batchSize: Int): Unit = {
        val filterList = new FilterList()
        filterList.addFilter(new RowFilter(CompareOperator.EQUAL, new RegexStringComparator(keyRegexp)))
        filterList.addFilter(new KeyOnlyFilter())
        val iterator = scan(table, filterList)
        LoanPattern.using(connection.getTable(TableName.valueOf(table))) {
            t =>
                iterator.map(_.getRow).grouped(batchSize)
                    .map(_.map(new Delete(_)))
                    .foreach {
                        deletes => t.delete(deletes.asJava)
                    }
        }
    }

    def scan(table: String, filterList: FilterList = null): util.Iterator[Result] = {
        val hTable = connection.getTable(TableName.valueOf(table))
        val scan = new Scan()
        if (filterList != null)
            scan.setFilter(filterList)
        hTable.getScanner(scan).iterator()
    }

    def bulkPut(table: String, puts: List[Put], batchSize: Int): Unit = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table))) {
            t =>
                puts.grouped(batchSize)
                    .foreach {
                        ps => t.put(ps.asJava)
                    }
        }
    }

    def clone(snapshot: String, table: String): Unit = {
        LoanPattern.using(connection.getAdmin)(admin => admin.cloneSnapshot(snapshot, TableName.valueOf(table)))
    }

    def count(table: String): Int = {
        val tableName = TableName.valueOf(table)
        LoanPattern.using(connection.getTable(tableName))(table => table.getScanner(new Scan().setFilter(new FirstKeyOnlyFilter)).count(_ => true))
    }

    /**
     * 创建命名空间
     *
     * @param namespace 命名空间
     */
    def createNamespace(namespace: String): Unit = {
        val result = Try {
            LoanPattern.using(connection.getAdmin)(admin => {
                if (!admin.listNamespaceDescriptors().map(_.getName).contains(namespace)) {
                    this.logInfo(s"Start create namespace $namespace")
                    admin.createNamespace(NamespaceDescriptor.create(namespace).build)
                    this.logInfo(s"Namespace $namespace was successfully created")
                }
            })
        }
        if (result.isFailure)
            result.failed.get match {
                case _: NamespaceExistException => this.logError("Try to create an existed namespace, exception was avoided by program")
                case _ => throw result.failed.get
            }
    }

    /**
     * 创建表
     *
     * @param table          表名称
     * @param columnFamilies 列族数组
     * @param startKey       RowKey起始
     * @param endKey         RowKey终止
     * @param regionNumber   Region数目
     */
    def createTable(table: String, columnFamilies: Array[String], startKey: String = null, endKey: String = null, regionNumber: Int = 0): Unit = this.createTable(table, columnFamilies.map(_ -> 1).toMap, startKey, endKey, regionNumber)

    /**
     * 创建表
     *
     * @param table                表名称
     * @param columnFamiliesSchema 列族和版本数数组
     * @param startKey             RowKey起始
     * @param endKey               RowKey终止
     * @param regionNumber         Region数目
     */
    def createTable(table: String, columnFamiliesSchema: Map[String, Int], startKey: String, endKey: String, regionNumber: Int): Unit = {
        val result = Try {
            LoanPattern.using(connection.getAdmin)(admin => {
                val tableName = TableName.valueOf(table)
                if (!admin.tableExists(tableName)) {
                    val descriptor = new HTableDescriptor(tableName)
                    TableDescriptorBuilder.newBuilder(tableName).build()
                    columnFamiliesSchema.foreach(cfs => {
                        val columnFamily = new HColumnDescriptor(cfs._1)
                        columnFamily.setMaxVersions(cfs._2)
                        descriptor.addFamily(columnFamily)
                    })
                    if (startKey.isNull || endKey.isNull || regionNumber <= 0)
                        admin.createTable(descriptor)
                    else
                        admin.createTable(descriptor, startKey.getBytes, endKey.getBytes, regionNumber)
                    this.logInfo(s"HBase table $table was successfully created")
                }
            })
        }
        if (result.isFailure)
            result.failed.get match {
                case _: TableExistsException =>
                case t: Throwable => throw t
            }
    }

    /**
     * HBase删除单行单列
     *
     * @param table     HBase表名
     * @param key       rowkey
     * @param family    列族
     * @param qualifier 列名
     */
    def delete(table: String, key: String, family: String = null, qualifier: String = null, timestamp: Long = 0): Unit = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table)))(hTable => {
            val delete = new Delete(Bytes.toBytes(key))
            if (family.nonEmpty && qualifier.nonEmpty) {
                if (timestamp > 0)
                    delete.addColumns(family.getBytes, qualifier.getBytes, timestamp)
                else
                    delete.addColumns(family.getBytes, qualifier.getBytes)
            }
            hTable.delete(delete)
        })
    }

    /**
     * 删除命名空间
     *
     * @param namespace 命名空间
     * @param force     若命名空间非空，则需指定此项为true，否则将抛出异常
     */
    def deleteNamespace(namespace: String, force: Boolean): Unit = {
        val result = Try {
            LoanPattern.using(connection.getAdmin)(admin => {
                val tables = admin.listTableNamesByNamespace(namespace)
                if (force) {
                    tables.foreach(table => {
                        if (admin.isTableEnabled(table))
                            admin.disableTable(table)
                        admin.deleteTable(table)
                    })
                }
                admin.deleteNamespace(namespace)
            })
        }
        if (result.isFailure)
            result.failed.get match {
                case _: NamespaceNotFoundException => this.logError("Try to drop an unexisted namespace, exception was avoided by program")
                case t: Throwable => throw t
            }
    }

    def deleteSnapshot(snapshot: String): Unit = {
        LoanPattern.using(connection.getAdmin)(_.deleteSnapshots(snapshot.r.pattern))
    }

    /**
     * 禁用表
     *
     * @param table 表名称
     */
    def disableTable(table: String): Unit = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            if (admin.isTableEnabled(tableName))
                admin.disableTable(tableName)
        })
    }

    /**
     * 删除表
     *
     * @param table 表名称
     */
    def dropTable(table: String): Unit = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            if (admin.tableExists(tableName)) {
                admin.deleteTable(tableName)
            }
        })
    }

    def enableTable(table: String): Unit = {
        val tableName = TableName.valueOf(table)
        LoanPattern.using(connection.getAdmin)(admin => {
            if (admin.isTableDisabled(tableName))
                admin.enableTable(tableName)
        })
    }

    def exists(table: String, key: String): Boolean = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table)))(_.exists(new Get(Bytes.toBytes(key))))
    }

    def existsAll(table: String, keys: List[String]): Map[String, Boolean] = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table)))(keys zip _.exists(keys.map(Bytes.toBytes).map(new Get(_)))).toMap
    }

    /**
     * 遍历一张HBase表
     *
     * @param table 表名
     * @return
     */
    def get(table: String): List[Result] = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table)))(hTable => {
            val resultList = ListBuffer[Result]()
            val iterator = hTable.getScanner(new Scan()).iterator()
            while (iterator.hasNext)
                resultList += iterator.next()
            resultList.toList
        })
    }

    /**
     * 读取HBase表中的一行
     *
     * @param table 表名
     * @param key   rowkey
     * @return
     */
    def get(table: String, key: String): Result = {
        LoanPattern.using(connection.getTable(TableName.valueOf(table)))(_.get(new Get(Bytes.toBytes(key))))
    }

    /**
     * 计数列增加
     *
     * @param table     HBase表名
     * @param key       rowkey
     * @param family    列族
     * @param qualifier 列名
     * @param howMuch   增加多少
     * @return
     */
    def increase(table: String, key: String, family: String, qualifier: String, howMuch: Long): Long = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            LoanPattern.using(connection.getTable(tableName))(hTable => {
                if (admin.isTableDisabled(tableName))
                    admin.enableTable(tableName)
                hTable.incrementColumnValue(key.getBytes, family.getBytes, qualifier.getBytes, howMuch)
            })
        })
    }

    def list(namespace: String = null, pattern: String = null): Array[String] = {
        LoanPattern.using(connection.getAdmin)(admin => {
            if (namespace.isNullOrEmpty)
                if (pattern.isNullOrEmpty)
                    admin.listTableNames()
                else
                    admin.listTableNames(pattern.r.pattern)
            else if (pattern.isNullOrEmpty)
                admin.listTableNamesByNamespace(namespace)
            else
                admin.listTableNamesByNamespace(namespace).filter(_.getNameAsString.matches(pattern))
        }).map(_.getNameAsString)
    }

    def listNamespaces(): Array[String] = {
        LoanPattern.using(connection.getAdmin)(admin => {
            admin.listNamespaceDescriptors()
        }).map(_.getName)
    }

    def listSnapshot(pattern: String = null): List[List[String]] = {
        LoanPattern.using(connection.getAdmin)(admin => {
            if (pattern.isNullOrEmpty)
                admin.listSnapshots()
            else
                admin.listSnapshots(pattern.r.pattern)
        }).map(s => List(s.getName, s.getTableNameAsString, DateTimeUtils.format(s.getCreationTime, "yyyy-MM-dd HH:mm:ss"))).toList
    }

    /**
     * 向HBase表插入一条数据
     *
     * @param table  表名
     * @param key    rowkey
     * @param family 列族
     * @param qvPair 列——值对
     */
    def put(table: String, key: String, family: String, qvPair: Map[String, String]): Unit = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            LoanPattern.using(connection.getTable(tableName))(hTable => {
                val put = new Put(Bytes.toBytes(key))
                if (admin.isTableDisabled(tableName))
                    admin.enableTable(tableName)
                qvPair.foreach(pair => put.addColumn(Bytes.toBytes(family), Bytes.toBytes(pair._1), Bytes.toBytes(pair._2)))
                hTable.put(put)
            })
        })
    }

    /**
     * 创建快照
     *
     * @param name     HBase表名称
     * @param snapshot 快照名称
     */
    def snapshot(name: String, snapshot: String): Unit = {
        val result = Try {
            LoanPattern.using(connection.getAdmin)(admin => {
                val tableName = TableName.valueOf(name)
                if (admin.tableExists(tableName))
                    if (!admin.listSnapshots().map(_.getName).contains(snapshot)) {
                        admin.snapshot(snapshot, tableName)
                        this.logInfo(s"Snapshot “$snapshot” of “$tableName” was successfully created")
                    }
            })
        }
        if (result.isFailure)
            result.failed.get match {
                case _: SnapshotExistsException =>
                case _: SnapshotCreationException =>
                case e: Throwable =>
                    this.logError(s"Exception occurred but avoided by program, detail information is bellow")
                    e.printStackTrace()
            }
    }

    /**
     * 判断表是否存在
     *
     * @param table 表名称
     * @return Boolean
     */
    def tableExists(table: String): Boolean = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            admin.tableExists(tableName)
        })
    }

    /**
     * 清空表
     *
     * @param table 表名称
     */
    def truncateTable(table: String): Unit = {
        LoanPattern.using(connection.getAdmin)(admin => {
            val tableName = TableName.valueOf(table)
            if (admin.tableExists(tableName)) {
                if (admin.isTableEnabled(tableName))
                    admin.disableTable(tableName)
                admin.truncateTable(tableName, false)
                if (!admin.isTableEnabled(tableName))
                    admin.enableTable(tableName)
            }
        })
    }

}
