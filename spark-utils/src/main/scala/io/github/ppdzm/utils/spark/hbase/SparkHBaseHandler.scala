package io.github.ppdzm.utils.spark.hbase

import io.github.ppdzm.utils.universal.feature.ExceptionGenerator
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.spark.HBaseContext
import org.apache.spark.sql.DataFrame
import io.github.ppdzm.utils.hadoop.hbase.implicts.HBaseImplicits._
import io.github.ppdzm.utils.hadoop.hbase.{HBaseCatalog, HBaseEnvironment}
import io.github.ppdzm.utils.spark.SparkUtils
import io.github.ppdzm.utils.spark.implicits.DataFrameConversions._
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

case class SparkHBaseHandler(zookeeperQuorum: String, zookeeperPort: Int = 2181) extends HBaseEnvironment {
    protected lazy val hBaseContext = new HBaseContext(sparkSession.sparkContext, configuration)
    private lazy val sparkSession = SparkUtils.getSparkSession()

    import sparkSession.implicits._

    def df(catalog: HBaseCatalog): DataFrame = {
        val columns = catalog.columns
        if (columns.isEmpty)
            throw ExceptionGenerator.newException("NoQualifier", "Qualifier of HBaseCatalog is empty")
        val name = catalog.table.name
        val hBaseRDD = this.hBaseContext.hbaseRDD(TableName.valueOf(name), new Scan()).map(_._2)
        val rk = catalog.columns.find(c => c._2.cf == HBaseCatalog.doNotChangeMeFamily && c._2.col == HBaseCatalog.doNotChangeMeColumn).get._1
        val fields = catalog.columns.filterNot(_._2.cf == HBaseCatalog.doNotChangeMeFamily).filterNot(_._2.col == HBaseCatalog.doNotChangeMeColumn).keys.toList
        val hasRowKey = rk.notNullAndEmpty
        val number = if (hasRowKey)
            fields.length + 1
        else
            fields.length
        val cols = if (hasRowKey)
            rk :: fields
        else
            fields
        val dataFrame: DataFrame = number match {
            case 0 => throw ExceptionGenerator.newException("AtLeastOneQualifierProvided", "Since RowKey isn't in the Qualifier list for query, at least one Qualifier should be provided")
            case 1 => hBaseRDD.map(_.tuple1(fields, columns, hasRowKey)).toDF(cols: _*)
            case 2 => hBaseRDD.map(_.tuple2(fields, columns, hasRowKey)).toDF(cols: _*)
            case 3 => hBaseRDD.map(_.tuple3(fields, columns, hasRowKey)).toDF(cols: _*)
            case 4 => hBaseRDD.map(_.tuple4(fields, columns, hasRowKey)).toDF(cols: _*)
            case 5 => hBaseRDD.map(_.tuple5(fields, columns, hasRowKey)).toDF(cols: _*)
            case 6 => hBaseRDD.map(_.tuple6(fields, columns, hasRowKey)).toDF(cols: _*)
            case 7 => hBaseRDD.map(_.tuple7(fields, columns, hasRowKey)).toDF(cols: _*)
            case 8 => hBaseRDD.map(_.tuple8(fields, columns, hasRowKey)).toDF(cols: _*)
            case 9 => hBaseRDD.map(_.tuple9(fields, columns, hasRowKey)).toDF(cols: _*)
            case 10 => hBaseRDD.map(_.tuple10(fields, columns, hasRowKey)).toDF(cols: _*)
            case 11 => hBaseRDD.map(_.tuple11(fields, columns, hasRowKey)).toDF(cols: _*)
            case 12 => hBaseRDD.map(_.tuple12(fields, columns, hasRowKey)).toDF(cols: _*)
            case 13 => hBaseRDD.map(_.tuple14(fields, columns, hasRowKey)).toDF(cols: _*)
            case 14 => hBaseRDD.map(_.tuple14(fields, columns, hasRowKey)).toDF(cols: _*)
            case 15 => hBaseRDD.map(_.tuple15(fields, columns, hasRowKey)).toDF(cols: _*)
            case 16 => hBaseRDD.map(_.tuple16(fields, columns, hasRowKey)).toDF(cols: _*)
            case 17 => hBaseRDD.map(_.tuple17(fields, columns, hasRowKey)).toDF(cols: _*)
            case 18 => hBaseRDD.map(_.tuple18(fields, columns, hasRowKey)).toDF(cols: _*)
            case 19 => hBaseRDD.map(_.tuple19(fields, columns, hasRowKey)).toDF(cols: _*)
            case 20 => hBaseRDD.map(_.tuple20(fields, columns, hasRowKey)).toDF(cols: _*)
            case 21 => hBaseRDD.map(_.tuple21(fields, columns, hasRowKey)).toDF(cols: _*)
            case 22 => hBaseRDD.map(_.tuple22(fields, columns, hasRowKey)).toDF(cols: _*)
            case _ => throw ExceptionGenerator.newException("TooMoreQualifier", "Qualifier number of HBaseCatalog can't be more than 22, it's a limitation of Scala Tuple")
        }
        dataFrame.cast(fields.map(f => f -> columns(f).`type`).toMap)
    }

    /**
     * 根据HBase表的Catalog将一张HBase表加载为一个DataFrame
     *
     * @param catalog HBaseCatalog
     * @return
     */
    def load(catalog: String): DataFrame = {
        sparkSession.read.options(Map("catalog" -> catalog)).format("org.apache.hadoop.hbase.spark").load()
    }

    /**
     * 根据HBase表的Catalog将一张HBase表加载为一个DataFrame
     *
     * @param catalog HBaseCatalog
     * @return
     */
    def load(catalog: HBaseCatalog): DataFrame = {
        sparkSession.read.options(Map("catalog" -> catalog.toString)).format("org.apache.hadoop.hbase.spark").load()
    }

    /**
     * 根据HBase表的Catalog将一张HBase表加载为一个DataFrame
     *
     * @param namespace HBase表所在的命名空间
     * @param name      HBase表名称，非default下的表需要写全称
     * @param rowkey    rowkey在DataFrame中的列名
     * @param columns   DataFrame列名->(列族名,列名,类型)
     * @return
     */
    def load(namespace: String, name: String, rowkey: String, columns: Map[String, (String, String, String)]): DataFrame = {
        sparkSession.read.options(Map("catalog" -> HBaseCatalog(namespace, name, rowkey, columns).toString)).format("org.apache.hadoop.hbase.spark").load()
    }

}
