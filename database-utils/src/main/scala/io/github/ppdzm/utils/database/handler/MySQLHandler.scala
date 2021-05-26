package io.github.ppdzm.utils.database.handler

import java.sql.{ResultSet, ResultSetMetaData}
import java.util

import io.github.ppdzm.utils.database.connection.MySQLConnection
import io.github.ppdzm.utils.universal.base.{Logging, StringUtils}
import io.github.ppdzm.utils.universal.feature.LoanPattern
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.ResultSetConversions._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * Created by Stuart Alex on 2018-04-18
 */
case class MySQLHandler(url: String, extraProperties: Map[String, AnyRef]) extends RDBHandler with Logging {

    /**
     * 查询binlog_format
     *
     * @return
     */
    def binlogFormat(): String = {
        query("show variables like 'binlog_format'").scalar(0, 1)
    }

    /**
     * 检查主键类型与值是否匹配
     *
     * @param primaryKeyName  主键字段名称
     * @param primaryKeyValue 主键值
     * @param primaryKeyType  主键类型
     * @return
     */
    def checkPrimaryKeyType(primaryKeyName: String, primaryKeyValue: Any, primaryKeyType: String): Boolean = {
        val valid = checkColumnType(primaryKeyValue, primaryKeyType)
        if (!valid) {
            logInfo(s"pk $primaryKeyName $primaryKeyValue not match $primaryKeyType")
        }
        valid
    }

    /**
     * 检查值与类型是否匹配
     *
     * @param value      字段值
     * @param columnType 字段类型
     * @return
     */
    def checkColumnType(value: Any, columnType: String): Boolean = {
        columnType match {
            case "varchar" => value.isInstanceOf[String]
            case "bigint" => value.isInstanceOf[Long]
            case "int" => value.isInstanceOf[Int]
            case "tinyint" => value.isInstanceOf[Int]
            case "boolean" => value.isInstanceOf[Boolean]
            case "float" => value.isInstanceOf[Float]
            case "double" => value.isInstanceOf[Double]
            case otherType => throw new Exception(s"$otherType is not processed")
        }
    }

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     */
    def createDatabase(database: String): Unit = {
        execute(s"create database if not exists $database")
    }

    /**
     * 创建表
     *
     * @param createSql 建表语句
     */
    def createTable(createSql: String): Unit = {
        execute(createSql)
    }

    /**
     * 创建表
     *
     * @param database         数据库名称
     * @param table            表名称
     * @param columnDefinition 表字段定义
     */
    def createTable(database: String, table: String, columnDefinition: Map[String, String]): Unit = {
        execute(s"create table $database.$table(${columnDefinition.map(e => e._1 + " " + e._2).mkString(",")})")
    }

    /**
     * 使用主键删除单条数据
     *
     * @param table           表名称
     * @param primaryKeyName  主键字段名称
     * @param primaryKeyValue 主键值
     */
    def delete(table: String, primaryKeyName: String, primaryKeyValue: Any): Unit = {
        val sql = s"delete from $table where $primaryKeyName=?"
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
            ps =>
                ps
                    .setParameter(1, primaryKeyValue)
                    .executeUpdate()
        }
    }

    /**
     * 使用主键删除单条数据
     *
     * @param table           表名称
     * @param primaryKeyName  主键字段名称
     * @param primaryKeyValue 主键值
     * @param primaryKeyType  主键类型
     */
    def delete(table: String, primaryKeyName: String, primaryKeyValue: Any, primaryKeyType: String): Unit = {
        val sql = s"delete from $table where $primaryKeyName=?"
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
            ps =>
                ps
                    .setParameter(1, primaryKeyValue, primaryKeyType)
                    .executeUpdate()
        }
    }

    /**
     * 删除数据库
     *
     * @param database 数据库名称
     * @param cascade  若数据库非空，则需指定cascade，否则将抛出异常
     */
    def dropDatabase(database: String, cascade: Boolean): Unit = {
        execute(s"drop database if exists $database")
    }

    /**
     * 删除表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def dropTable(database: String, table: String): Unit = {
        execute(s"drop table if exists ${url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'))}.$table")
    }

    /**
     * 执行sql
     *
     * @param statement  sql语句
     * @param parameters sql语句参数
     * @return Boolean（是否成功）
     */
    def execute(statement: String, parameters: Array[String]): Boolean = {
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, properties = extraProperties, sql = statement)) {
            ps =>
                parameters
                    .zipWithIndex
                    .filter { case (v, _) => v != null }
                    .foreach { case (v, i) => ps.setObject(i + 1, v) }
                ps.execute()
        }
    }

    /**
     * 判断某个数据库是否存在
     *
     * @param database 数据库名称
     * @return
     */
    def exists(database: String): Boolean = {
        listDatabases().contains(database)
    }

    /**
     * 列出所有数据库
     *
     * @return
     */
    def listDatabases(regexp: String = null): List[String] = {
        if (regexp.notNullAndEmpty)
            query("show databases").singleColumnList(0).filter(_.matches(regexp))
        else
            query("show databases").singleColumnList(0)
    }

    /**
     * 判断某张表是否存在
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def exists(database: String, table: String): Boolean = {
        listTables(database).contains(table)
    }

    /**
     * 列出所有表
     *
     * @param database 数据库名称
     * @return
     */
    def listTables(database: String, regexp: String = null): List[String] = {
        val database = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"))
        val sql = s"select table_name from information_schema.tables where table_schema='$database'"
        if (regexp.notNullAndEmpty)
            query(sql).singleColumnList(0).filter(_.matches(regexp))
        else
            query(sql).singleColumnList(0)
    }

    /**
     * 获取字段类型
     *
     * @param tableName 表名称
     * @return
     */
    def getColumnsType4Java(tableName: String): util.Map[String, String] = {
        getColumnsType(tableName).asJava
    }

    /**
     * 获取字段类型
     *
     * @param tableName 表名称
     * @return
     */
    def getColumnsType(tableName: String): Map[String, String] = {
        val sql = s"select * from $tableName where 1<0"
        val conn = MySQLConnection.getConnection(url, extraProperties)
        val stmt = conn.createStatement()
        val res = stmt.executeQuery(sql)
        getColumnsType(res.getMetaData)
    }

    /**
     * 获取字段类型
     *
     * @param resultSetMetaData ResultSetMetaData
     * @return
     */
    def getColumnsType(resultSetMetaData: ResultSetMetaData): Map[String, String] = {
        val columnCount = resultSetMetaData.getColumnCount
        (1 to columnCount).map {
            columnIndex =>
                val columnName = resultSetMetaData.getColumnName(columnIndex)
                val columnType = resultSetMetaData.getColumnTypeName(columnIndex).split(" ").head.toLowerCase
                (columnName, columnType)
        }.toMap
    }

    /**
     * 查询global binlog_format
     *
     * @return
     */
    def globalBinlogFormat(): String = {
        query("show global variables like 'binlog_format'").scalar(0, 1)
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return
     */
    def query(sql: String): ResultSet = {
        query(sql, null)
    }

    /**
     * 查询
     *
     * @param sql        sql语句
     * @param parameters sql语句参数
     * @return
     */
    def query(sql: String, parameters: Array[Any]): ResultSet = {
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, properties = extraProperties, sql = sql)) {
            ps =>
                ps
                    .setParameters(parameters)
                    .executeQuery()
        }
    }

    /**
     * 批量插入或更新数据
     *
     * @param table              表名称
     * @param primaryKeyName     主键字段名称
     * @param columnNameValueMap （（字段——值）对）列表
     * @param columnNameTypePair （字段——类型）对
     * @return
     */
    def insertOrUpdate(table: String,
                       primaryKeyName: String,
                       columnNameValueMap: util.Map[String, Any],
                       columnNameTypePair: util.Map[String, String]): Int = {
        insertOrUpdate(table, primaryKeyName, columnNameValueMap.asScala, columnNameTypePair.asScala)
    }

    /**
     * 批量插入或更新数据
     *
     * @param table              表名称
     * @param primaryKeyName     主键字段名称
     * @param columnNameValueMap （字段——值）对
     * @param columnNameTypePair （字段——类型）对
     * @return
     */
    def insertOrUpdate(table: String,
                       primaryKeyName: String,
                       columnNameValueMap: Map[String, Any],
                       columnNameTypePair: Map[String, String]): Int = {
        val validColumnNameValuePair = columnNameValueMap.filter(_._2 != null)
        if (!validColumnNameValuePair.contains(primaryKeyName)) {
            return 0
        }
        val columnsCount = validColumnNameValuePair.keys.size
        val sql = generateInsertOrUpdateString(table, validColumnNameValuePair)
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
            ps =>
                ps
                    .setParameters(validColumnNameValuePair, columnNameTypePair, 1)
                    .setParameters(validColumnNameValuePair, columnNameTypePair, columnsCount + 1)
                    .executeUpdate()
        }
    }

    /**
     * 生成 insert or update 语句
     *
     * @param table              表名称
     * @param columnNameValueMap 字段——值Map
     */
    def generateInsertOrUpdateString(table: String, columnNameValueMap: Map[String, Any]): String = {
        val sortedColumns = columnNameValueMap.keySet.toList.sorted
        val columnsCount = sortedColumns.length
        s"""
           |insert into $table (${sortedColumns.mkString(",")})
           |values (${Array.fill(columnsCount)("?").mkString(",")})
           |on duplicate key
           |update ${sortedColumns.map { key => s"$key=?" }.mkString(", ")}
           """.stripMargin
    }

    /**
     * 批量插入或更新数据
     *
     * @param table                   表名称
     * @param columnNameValuePairList （（字段——值）对）列表
     * @param columnNameTypePair      （字段——类型）对
     */
    def insertOrUpdateInBatch(table: String, columnNameValuePairList: List[Map[String, Any]], columnNameTypePair: Map[String, String]): List[Int] = {
        if (columnNameValuePairList.isEmpty) {
            return List()
        }
        val columnNameValueMap = columnNameValuePairList.head
        val columnsCount = columnNameValueMap.keys.size
        val sql = generateInsertOrUpdateString(table, columnNameValueMap)
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
            ps =>
                columnNameValuePairList.foreach {
                    columnNameValuePair =>
                        ps
                            .setParameters(columnNameValuePair, columnNameTypePair, 1)
                            .setParameters(columnNameValuePair, columnNameTypePair, columnsCount + 1)
                            .addBatch()
                }
                ps.executeBatch().toList
        }
    }

    /**
     * 查询
     *
     * @param sql     sql语句
     * @param columns 所需结果字段
     * @return
     */
    def query(sql: String, columns: util.Collection[String]): util.Iterator[util.Map[String, Any]] = {
        query(sql, columns.toList)
            .map { e => e.asJava }
            .asJava
    }

    /**
     * 查询
     *
     * @param sql     sql语句
     * @param columns 所需结果字段
     * @return
     */
    def query(sql: String, columns: Iterable[String]): Iterator[Map[String, Any]] = {
        LoanPattern.using(query(sql)) {
            resultSet =>
                val columnNameTypePair = getColumnsType(resultSet.getMetaData)

                new Iterator[Map[String, Any]] {
                    def hasNext: Boolean = resultSet.next()

                    def next(): Map[String, Any] = {
                        columns.map {
                            column =>
                                val columnType = columnNameTypePair(column)
                                val value =
                                    columnType match {
                                        case "varchar" => resultSet.getString(column)
                                        case "bigint" => resultSet.getLong(column)
                                        case "int" => resultSet.getLong(column)
                                        case "tinyint" => resultSet.getInt(column)
                                        case "boolean" => resultSet.getBoolean(column)
                                        case "float" => resultSet.getFloat(column)
                                        case "double" => resultSet.getDouble(column)
                                        case otherType =>
                                            throw new Exception(s"$otherType is not processed")
                                    }
                                (column, value)
                        }.toMap
                    }
                }
        }
    }

    /**
     * 随机向某张表内插入一些数据
     *
     * @param table   目标表名称
     * @param columns 目标表字段列表
     * @param count   插入随机数字的行数
     */
    def randomInsert(table: String, columns: Array[String], count: Int): Unit = {
        LoanPattern.using(MySQLConnection.getConnection(url, extraProperties))(connection => {
            connection.setAutoCommit(false)
            val statement = s"insert into $table(${columns.mkString(",")}) values (${List.fill(columns.length)("?").mkString(",")})"
            val ps = connection.prepareStatement(statement)
            (1 to count).foreach(_ => {
                ps
                    .setParameters(Array.fill(columns.length)(StringUtils.randomString(20)))
                    .addBatch()
            })
            ps.executeBatch()
            connection.commit()
        })
    }

    /**
     * 查询hive外部表创建语句
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def showCreateTable(database: String, table: String): String = {
        query(s"show create table $database.$table").singleColumnList(0).mkString(" ")
    }

    /**
     * 清空表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def truncate(database: String, table: String): Unit = {
        execute(s"truncate table $database.$table")
    }

    /**
     * 执行sql语句
     *
     * @param statement sql语句
     */
    def execute(statement: String): Unit = {
        LoanPattern.using(MySQLConnection.getStatement(url, extraProperties))(ps => {
            ps.execute(statement)
        })
    }

    /**
     * 更新数据
     *
     * @param table                   表名称
     * @param singlePrimaryKey        主键字段名称
     * @param columnNameValuePairList （（字段——值）对）列表
     * @param columnNameTypePair      （字段——类型）对
     */
    def update(url: String,
               table: String,
               singlePrimaryKey: String,
               columnNameValuePairList: util.Map[String, Any],
               columnNameTypePair: util.Map[String, String]): Int = {
        update(url, table, singlePrimaryKey, columnNameValuePairList.asScala, columnNameTypePair.asScala)
    }

    /**
     * 更新数据
     *
     * @param table               表名称
     * @param singlePrimaryKey    主键字段名称
     * @param columnNameValuePair （（字段——值）对）列表
     * @param columnNameTypePair  （字段——类型）对
     * @return
     */
    def update(table: String,
               singlePrimaryKey: String,
               columnNameValuePair: Map[String, Any],
               columnNameTypePair: Map[String, String]): Int = {
        val validColumnNameValuePair = columnNameValuePair.filter(_._2 != null)
        if (!validColumnNameValuePair.contains(singlePrimaryKey)) {
            return 0
        }
        val updateString = fieldsMapToUpdateString(validColumnNameValuePair.keys.toList.sorted)
        val sql = s"update $table set $updateString where $singlePrimaryKey=?"
        val columnsCount = validColumnNameValuePair.keys.size
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
            ps =>
                val primaryKeyValue = validColumnNameValuePair(singlePrimaryKey)
                val primaryKeyType = columnNameTypePair(singlePrimaryKey)
                ps
                    .setParameters(validColumnNameValuePair, columnNameTypePair, 1)
                    .setParameter(columnsCount + 1, primaryKeyValue, primaryKeyType)
                    .executeUpdate

        }
    }

    private def fieldsMapToUpdateString(fields: Iterable[String]) = {
        fields.map { key => s"$key=?" }.mkString(", ")
    }

    /**
     * 更新数据
     *
     * @param table                   表名称
     * @param singlePrimaryKey        主键字段名称
     * @param columnNameValuePairList （（字段——值）对）列表
     * @param columnNameTypePair      （字段——类型）对
     */
    def updateInBatch(table: String,
                      singlePrimaryKey: String,
                      columnNameValuePairList: List[Map[String, Any]],
                      columnNameTypePair: Map[String, String]): Unit = {
        columnNameValuePairList
            .map {
                columnNameValuePair =>
                    val validColumnNameValuePair = columnNameValuePair.filter(_._2 != null)
                    val updateString = fieldsMapToUpdateString(validColumnNameValuePair.keys.toList.sorted)
                    (updateString, validColumnNameValuePair)
            }
            .groupBy(_._1)
            .mapValues(_.map(_._2))
            .foreach {
                case (updateString, columnNameValuePair) =>
                    val sql = s"update $table set $updateString where $singlePrimaryKey=?"
                    LoanPattern.using(MySQLConnection.getPreparedStatement(url, extraProperties, sql)) {
                        ps =>
                            columnNameValuePair
                                .filter { columnNameValuePair => columnNameValuePair.contains(singlePrimaryKey) }
                                .foreach {
                                    columnNameValuePair =>
                                        val columnsCount = columnNameValuePair.keys.toList.length
                                        val primaryKeyValue = columnNameValuePair(singlePrimaryKey)
                                        val primaryKeyType = columnNameTypePair(singlePrimaryKey)
                                        ps
                                            .setParameters(columnNameValuePair, columnNameTypePair, 1)
                                            .setParameter(columnsCount + 1, primaryKeyValue, primaryKeyType)
                                            .addBatch()
                                }
                            ps.executeBatch()
                            ps.clearBatch()
                    }
            }
    }

}
