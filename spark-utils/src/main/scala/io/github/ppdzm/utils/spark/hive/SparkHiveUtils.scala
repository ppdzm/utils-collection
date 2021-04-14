package io.github.ppdzm.utils.spark.hive

import java.sql.ResultSet

import io.github.ppdzm.utils.database.handler.RDBHandler
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.feature.ExceptionGenerator
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.StructType
import org.sa.utils.spark.sql.SparkSQL
import org.sa.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Created by Stuart Alex on 2017/1/11.
 */
object SparkHiveUtils extends RDBHandler with Logging {
    override protected val url: String = "not supported"

    /**
     * Hive Over HBase表添加字段
     *
     * @param database     Hive数据库名称
     * @param table        Hive表名称
     * @param columnName   待添加的字段名称
     * @param columnType   待添加的字段类型
     * @param columnFamily 待添加的字段在HBase表中所属的列族名称
     * @param hColumnName  待添加的字段在HBase表中的字段名称
     * @return
     */
    def addColumn2ExternalHiveOverHBaseTable(database: String, table: String, columnName: String, columnType: String, columnFamily: String, hColumnName: String): Unit = {
        if (!this.isExternalHiveOverHBaseTable(database, table))
            throw ExceptionGenerator.newException("NotExternalHiveOverHBaseTable", "Can't doing add column on non-external-hive-over-hbase-table")
        if (SparkSQL.sql(s"desc $database.$table").select("col_name").collect().map(_.getString(0)).contains(columnName))
            throw ExceptionGenerator.newException("ColumnAlreadyExist", s"Column $columnName already exists in $database.$table")
        val statement = this.showCreateTable(database, table)
            .replace("CREATE EXTERNAL TABLE", "CREATE EXTERNAL TABLE if not exists")
            .replace(" COMMENT 'from deserializer'", "")
            .replaceSpecialSymbol
            .replaceFirst("""\)ROW""", s",$columnName $columnType) ROW")
            .replace("','serialization", s",$columnFamily:$hColumnName','serialization")
        this.execute(s"drop table if exists $database.$table")
        this.execute(statement)
    }

    /**
     * 判断一个hive表是否是hive over hbase外表
     *
     * @param database 数据库
     * @param table    表名
     * @return
     */
    def isExternalHiveOverHBaseTable(database: String, table: String): Boolean = {
        if (!exists(database))
            throw ExceptionGenerator.newException("DatabaseNotExist", s"Database $database does not exists")
        if (!exists(database, table))
            throw ExceptionGenerator.newException("TableNotExist", s"Table $database.$table does not exists")
        val createDdl = this.showCreateTable(database, table).toLowerCase
        createDdl.contains("create external table") && createDdl.contains("hbase.columns.mapping")
    }

    /**
     * 比较MySQL源数据表与Hive数据表的schema差异
     *
     * @param url            MySQL连接url
     * @param tableRegex     MySQL数据表正则表达式
     * @param hiveDatabase   Hive数据库名称
     * @param table          Hive数据表名称
     * @param ignoredMapping 忽略检查的字段类型映射
     */
    def compare(url: String, tableRegex: String, hiveDatabase: String, table: String, ignoredMapping: Map[String, String]): List[(String, String, String, String, String, String, String)] = {
        val database = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?')).toLowerCase
        val mts = SparkSQL.mysql.df(url.replace(database, "information_schema"), "columns")
            .filter(s"TABLE_SCHEMA='$database'")
            .select("column_name", "data_type", "table_name")
            .withColumnRenamed("column_name", "mysql_column_name")
            .withColumnRenamed("data_type", "mysql_data_type")
        val hts = SparkSQL.sql(s"desc $hiveDatabase.$table").select("col_name", "data_type")
            .withColumnRenamed("col_name", "hive_column_name")
            .withColumnRenamed("data_type", "hive_data_type")
        val joined = mts.join(hts, mts("mysql_column_name") === hts("hive_column_name"), "left").withColumn("hive_table_name", lit(s"$hiveDatabase.$table")).collect()
            .filter(row => row.getAs[String]("table_name").matches(tableRegex))
            .filter(row => {
                val mct = row.getAs[String]("mysql_data_type")
                val hct = row.getAs[String]("hive_data_type")
                if (mct == hct)
                    false
                else if (ignoredMapping.isNull)
                    true
                else if (ignoredMapping.contains(mct))
                    ignoredMapping(mct) != hct
                else
                    true
            })
        val differences = ListBuffer[(String, String, String, String, String, String, String)]()
        joined.foreach(row => {
            val mtn = row.getAs[String]("table_name")
            val mcn = row.getAs[String]("mysql_column_name")
            val mct = row.getAs[String]("mysql_data_type")
            val htn = row.getAs[String]("hive_table_name")
            val hcn = row.getAs[String]("hive_column_name")
            val hct = row.getAs[String]("hive_data_type")
            val conclusion = if (hcn.isNull)
                "Hive字段缺失"
            else
                "字段类型不同"
            differences += (("..." + url.substring(0, url.indexOf("?")).trimStart("jdbc:mysql://") + "...", mtn, htn, mcn, mct, hct, conclusion))
        })
        differences.toList
    }

    /**
     * 根据TableSchema创建Hive Over HBase表
     *
     * @param sourceDatabase      schema数据库名称
     * @param sourceTable         schema表名称
     * @param destinationDatabase 目标数据库
     * @param destinationTable    新建的hive表名称
     * @param hBaseTable          新建的hive表关联的hbase表名称
     * @param extras              额外添加的“列族——字段”配对
     * @return
     */
    def createExternalHiveOverHBaseTableFromTableSchema(sourceDatabase: String, sourceTable: String, destinationDatabase: String, destinationTable: String, hBaseTable: String, extras: (String, String)*): Unit = {
        if (!exists(destinationDatabase))
            createDatabase(destinationDatabase)
        if (exists(sourceDatabase, sourceTable) && !exists(destinationDatabase, destinationTable)) {
            this.logInfo(s"Create HiveOverHBaseTable $destinationDatabase.$destinationTable from schema of $sourceDatabase.$sourceTable")
            var statement = this.showCreateTable(sourceDatabase, sourceTable).replace(" COMMENT 'from deserializer'", "")
            statement = "(?<=CREATE EXTERNAL TABLE) `.*?(?=`)".r.replaceFirstIn(statement, s" if not exists `$destinationDatabase.${destinationTable.replace("-", "_")}")
            statement = "(?<=hbase.table.name'=').*?(?=')".r.replaceFirstIn(statement, hBaseTable)
            statement = ",[^']*?'serialization.format'='\\d+'".r.replaceFirstIn(statement, "")
            extras.foreach(c => {
                statement = "\\)ROW".r.replaceFirstIn(statement, s",`${c._2}` string)ROW")
                statement = "'\\)TBLPROPERTIES".r.replaceFirstIn(statement, s",${c._1}:${c._2}')TBLPROPERTIES")
            })
            statement = ",[^']*?'transient_lastDdlTime'='\\d+'".r.replaceFirstIn(statement, "")
            val result = Try(this.execute(statement))
            if (result.isFailure)
                result.failed.get match {
                    case _: Exception => this.logError(s"Error occurred while create external Hive over HBase table from table schema was ignored and statement is\n$statement")
                }
        }
    }

    /**
     * 查询hive外部表创建语句
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def showCreateTable(database: String, table: String): String = {
        SparkSQL.sql(s"show create table $database.$table").collect().map(_.getString(0).trim).mkString
    }

    /**
     * 根据DataFrame创建Hive Over HBase表
     *
     * @param database   Hive数据库名称
     * @param table      Hive表名称
     * @param schema     表结构
     * @param rowKey     表示rowkey的字段名称，要放在Hive表字段声明第一个位置
     * @param namespace  HBase命名空间
     * @param hBaseTable HBase表名称
     */
    def createExternalHiveOverHBaseTable(database: String, table: String, schema: StructType, rowKey: String, namespace: String, hBaseTable: String): Unit = {
        if (!exists(database))
            createDatabase(database)
        if (!exists(database, table)) {
            val types = schema.filter(_.name != rowKey).map(sf => s"${sf.name.toLowerCase} ${sf.dataType.typeName.toHiveDataType}")
            val statement = s"create external table if not exists $database.$table ($rowKey string," + types.mkString(",") +
                """) stored by 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' with serdeproperties ("hbase.columns.mapping"=":key,""" +
                schema.map("data:" + _.name.toLowerCase).mkString(",") +
                s"""") tblproperties ("hbase.table.name" = "$namespace:$hBaseTable")"""
            val result = Try(this.execute(statement))
            if (result.isFailure)
                result.failed.get match {
                    case _: Exception => this.logError(s"Error occurred while create external Hive over HBase table was ignored and statement is\n$statement")
                }
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
            SparkSQL.sql("show databases").collect().map(_.getString(0)).filter(_.matches(regexp)).toList
        else
            SparkSQL.sql("show databases").collect().map(_.getString(0)).toList
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
        if (regexp.notNullAndEmpty)
            SparkSQL.sql(s"show tables in $database").collect().map(_.getString(0)).filter(_.matches(regexp)).toList
        else
            SparkSQL.sql(s"show tables in $database").collect().map(_.getString(0)).toList
    }

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     */
    def createDatabase(database: String): Unit = {
        this.logInfo(s"Create hive database $database")
        this.execute(s"create database if not exists $database")
    }

    /**
     * 执行sql语句
     *
     * @param statement sql语句
     */
    def execute(statement: String): Unit = {
        SparkSQL.sql(statement)
    }

    /**
     * 创建表
     *
     * @param createSql 建表语句
     */
    def createTable(createSql: String): Unit = {
        exists(createSql)
    }

    /**
     * 创建表
     *
     * @param database         数据库名称
     * @param table            表名称
     * @param columnDefinition 表字段定义
     */
    def createTable(database: String, table: String, columnDefinition: Map[String, String]): Unit = {
        val types = columnDefinition.map(cf => s"${cf._1} ${cf._2.toHiveDataType}")
        if (!exists(database))
            createDatabase(database)
        if (!exists(database, table))
            execute(types.mkString(s"create table if not exists $database.$table (", ",", s") row format delimited fields terminated by '\t' stored as orc"))
    }

    /**
     * Hive Over HBase表删除字段
     *
     * @param database     Hive数据库名称
     * @param table        Hive表名称
     * @param columnName   待删除的字段名称
     * @param columnType   待删除的字段类型
     * @param columnFamily 待删除的字段在HBase表中所属的列族名称
     * @param hColumnName  待删除的字段在HBase表中的字段名称
     * @return
     */
    def dropColumnFromExternalHiveOverHBaseTable(database: String, table: String, columnName: String, columnType: String, columnFamily: String, hColumnName: String): Unit = {
        if (!this.isExternalHiveOverHBaseTable(database, table))
            throw ExceptionGenerator.newException("NotExternalHiveOverHBaseTable", "Can't doing drop column on non-external-hoh-table")
        if (!SparkSQL.sql(s"desc $database.$table").select("col_name").collect().map(_.getString(0)).contains(columnName))
            throw ExceptionGenerator.newException("ColumnNotExist", s"Column $columnName does not exists in $database.$table")
        val statement = this.showCreateTable(database, table)
            .replace("CREATE EXTERNAL TABLE", "CREATE EXTERNAL TABLE if not exists")
            .replace(" COMMENT 'from deserializer'", "")
            .replaceSpecialSymbol
            .replace(s",$columnName $columnType", "")
            .replace(s",$columnFamily:$hColumnName", "")
        this.execute(s"drop table if exists $database.$table")
        this.execute(statement)
    }

    /**
     * 删除数据库
     *
     * @param database 数据库名称
     * @param cascade  若数据库非空，则需指定cascade，否则将抛出异常
     */
    def dropDatabase(database: String, cascade: Boolean): Unit = {
        execute(s"drop database if exists $database${if (cascade) " cascade" else ""}")
    }

    /**
     * 删除表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def dropTable(database: String, table: String): Unit = {
        execute(s"drop table if exists $database.$table")
    }

    /**
     * 查询
     *
     * @param statement sql语句
     * @return
     */
    def query(statement: String): ResultSet = {
        throw new UnsupportedOperationException("not supported")
    }

    /**
     * 查询
     *
     * @param statement  sql语句
     * @param parameters sql语句参数
     * @return
     */
    def query(statement: String, parameters: Array[String]): ResultSet = {
        throw new UnsupportedOperationException("not supported")
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

}
