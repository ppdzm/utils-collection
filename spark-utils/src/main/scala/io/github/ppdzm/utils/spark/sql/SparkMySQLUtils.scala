package io.github.ppdzm.utils.spark.sql

import java.util.Properties

import io.github.ppdzm.utils.database.common.DatabaseConstants
import io.github.ppdzm.utils.spark.SparkUtils
import io.github.ppdzm.utils.spark.implicits.DataFrameConversions._
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.implicits.ArrayConversions._
import io.github.ppdzm.utils.universal.implicits.UnitConversions._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

object SparkMySQLUtils {
    protected lazy val logging = new Logging(getClass)
    private lazy val defaultProperties: Properties = DatabaseConstants.mySQLDefaultProperties

    /**
     * 把DataFrame中的数据写入MySQL
     *
     * @param url       MySQL连接字符串
     * @param table     目标表名称
     * @param dataFrame DataFrame
     * @param mode      保存模式
     * @return Boolean（是否成功）
     */
    def insert(url: String, table: String, dataFrame: DataFrame, mode: String): Unit = {
        this.logging.logInfo(s"Start write data from a DataFrame to table $table with mode $mode and url $url")
        dataFrame.write.mode(mode).jdbc(url, table, defaultProperties)
    }

    /**
     * 获取匹配传入的正则的表列表
     *
     * @param url   MySQL连接字符串
     * @param regex 正则表达式
     * @return
     */
    def listTables(url: String, regex: String): List[String] = this.listTables(url).filter(_.matches(regex))

    /**
     * 获取表列表
     *
     * @param url MySQL连接字符串
     * @return
     */
    def listTables(url: String): List[String] = {
        val database = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'))
        this.df(url.replace(database, "information_schema"), "TABLES")
            .filter(s"TABLE_SCHEMA='$database'")
            .select("TABLE_NAME")
            .collect()
            .map(_.getString(0))
            .toList
    }

    /**
     * 通过JDBC方式读取一张表
     * 对于List和Range分区表，采用predicates方式读取
     * 暂不处理Hash和Key分区表
     *
     * @param url   MySQL连接字符串
     * @param table 表名称
     * @return DataFrame
     */
    def df(url: String, table: String, properties: Properties = defaultProperties): DataFrame = {
        val database = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?')).toLowerCase
        if (database == "information_schema")
            SparkUtils.getSparkSession().read.jdbc(url, table, properties)
        else {
            val tableInfo = SparkUtils.getSparkSession().read.jdbc(url.replace(database, "information_schema"), "tables", properties)
                .filter(s"TABLE_SCHEMA='$database' and TABLE_NAME='$table'")
                .select("TABLE_NAME", "TABLE_ROWS", "CREATE_OPTIONS", "DATA_LENGTH")
                .collect()
            if (tableInfo.length == 1) {
                val size = tableInfo.head.get(3)
                this.logging.logInfo(s"Size of table $table is about ${
                    size.toString.toLong.toBytesLength
                }")
            }
            SparkUtils.getSparkSession().read.jdbc(url, table, properties).castBoolean2Int
        }
    }

    /**
     * 获取MySQL表的schema
     *
     * @param url   MySQL连接字符串
     * @param table 表名称
     * @return
     */
    def schema(url: String, table: String, properties: Properties = defaultProperties): StructType = {
        SparkUtils.getSparkSession().read.jdbc(url, table, properties).castBoolean2Int.schema
    }

    /**
     * 返回表的大致大小
     *
     * @param url   MySQL连接字符串
     * @param table 表名称
     * @return
     */
    def size(url: String, table: String, properties: Properties = defaultProperties): Long = {
        val database = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?')).toLowerCase
        SparkUtils.getSparkSession().read.jdbc(url.replace(database, "information_schema"), "tables", properties)
            .filter(s"TABLE_SCHEMA='$database' and TABLE_NAME='$table'")
            .select("DATA_LENGTH")
            .collect().head.get(0).toString.toLong
    }

    /**
     * 检查某张表是否有主键
     *
     * @param url   MySQL连接字符串
     * @param table 表名称
     * @return
     */
    def checkKey(url: String, table: String): (String, Boolean, Array[String]) = {
        val database = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'))
        val schema = this.df(url.replace(database, "information_schema"), "columns")
            .filter(s"TABLE_SCHEMA='$database'")
            .filter(s"TABLE_NAME='$table'")
            .filter("COLUMN_KEY='PRI'")
            .select("COLUMN_NAME")
            .collect().map(_.getString(0))
        if (schema.length != 0)
            (table, true, schema.ascending)
        else {
            this.logging.logWarning(s"Can not find primary key of table $table")
            (table, false, Array[String]())
        }
    }

}
