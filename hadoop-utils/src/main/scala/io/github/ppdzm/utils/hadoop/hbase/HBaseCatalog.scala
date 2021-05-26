package io.github.ppdzm.utils.hadoop.hbase

import java.util.Properties

import io.github.ppdzm.utils.universal.config.{Config, ConfigItem}
import io.github.ppdzm.utils.universal.feature.ExceptionGenerator
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

/**
 * Created by Stuart Alex on 2016/12/14.
 */
object HBaseCatalog {
    final val doNotChangeMeFamily = "rowkey"
    final val doNotChangeMeColumn = "key"
    private val rowKey = Qualifier(this.doNotChangeMeFamily, this.doNotChangeMeColumn, "string")

    def apply(namespace: String, name: String, rk: String, cf: String, columns: Array[String]): HBaseCatalog = {
        val fullName = if (name.startsWith(namespace + ":"))
            name
        else if (namespace != "")
            namespace + ":" + name
        else
            name
        val qualifiers = columns.map(col => col -> Qualifier(cf, col, "string")).+:(rk -> this.rowKey)
        HBaseCatalog(HBaseTable(namespace, fullName), this.doNotChangeMeColumn, qualifiers.toMap)
    }

    /**
     * 由配置生成HBaseCatalog
     *
     * @return
     */
    def apply(implicit config: Config): HBaseCatalog = {
        val namespace = new ConfigItem(config, "hbase.namespace", "default").stringValue
        val table = new ConfigItem(config, "hbase.table").stringValue
        assert(table.notNullAndEmpty, "parameter hbase.table is missing")
        val rowKeyAlias = new ConfigItem(config, "hbase.rowkey.alias", "rowkey").stringValue
        val family = new ConfigItem(config, "hbase.family.default", "data").stringValue
        val columnsDefinitions = new ConfigItem(config, "hbase.columns").arrayValue(",").map(c => {
            val fnt = new ConfigItem(config, s"hbase.column.$c", s"$family:$c:string").stringValue.split(":")
            fnt.length match {
                case 2 => c -> (fnt(0), fnt(1), "string")
                case 3 => c -> (fnt(0), fnt(1), fnt(2))
                case _ => throw ExceptionGenerator.newException("PropertyFormatError", s"Format of property hbase.column.$c is wrong, correct format is like family_name:column_name:data_type or family_name:column_name")
            }
        }).toMap
        HBaseCatalog(namespace, table, rowKeyAlias, columnsDefinitions)
    }

    /**
     * 生成HBaseCatalog
     *
     * @param namespace HBase表所在的命名空间
     * @param name      HBase表名称，非default下的表需要写全称
     * @param rk        rowkey在DataFrame中的列名
     * @param columns   DataFrame列名->(列族名,列名,类型)
     * @return
     */
    def apply(namespace: String, name: String, rk: String, columns: Map[String, (String, String, String)]): HBaseCatalog = {
        val fullName = if (name.startsWith(namespace + ":"))
            name
        else if (namespace != "")
            namespace + ":" + name
        else
            name
        val qualifiers = columns.map(c => c._1 -> Qualifier(c._2._1, c._2._2, c._2._3)) + (rk -> this.rowKey)
        HBaseCatalog(HBaseTable(namespace, fullName), this.doNotChangeMeColumn, qualifiers)
    }

    /**
     * 由配置生成HBaseCatalog
     *
     * @param properties Properties
     * @return
     */
    def apply(properties: Properties): HBaseCatalog = {
        val namespace = properties.getProperty("hbase.namespace", "default")
        val table = properties.getProperty("hbase.table")
        assert(table.notNullAndEmpty, "parameter hbase.table is missing")
        val rowKeyAlias = properties.getProperty("hbase.rowkey.alias", "rowkey")
        val columnsDefinitions = properties.getProperty("hbase.columns").split(",").map(_.trim).filter(_.notNullAndEmpty).map(c => {
            val fnt = properties.getProperty(s"hbase.column.$c", s"data:$c:string").split(":")
            fnt.length match {
                case 2 => c -> (fnt(0), fnt(1), "string")
                case 3 => c -> (fnt(0), fnt(1), fnt(2))
                case _ => throw ExceptionGenerator.newException("PropertyFormatError", s"Format of property hbase.column.$c is wrong, correct format is like cfn:cn:dt or cfn:cn")
            }
        }).toMap
        HBaseCatalog(namespace, table, rowKeyAlias, columnsDefinitions)
    }

    /**
     * 转换JSON为HBaseCatalog
     *
     * @param json JSON表示的HBase Catalog
     * @return
     */
    def apply(json: String): HBaseCatalog = JsonUtils.deserialize[HBaseCatalog](json, classOf[HBaseCatalog])

}

/**
 * HBase Catalog结构
 *
 * @param table   表名
 * @param rowkey  rowkey
 * @param columns 字段列表
 */
case class HBaseCatalog(table: HBaseTable, rowkey: String, columns: Map[String, Qualifier]) {

    /**
     * 打印
     */
    def display(render: String): Unit = {
        "HBase Catalog is:".prettyPrintln(render)
        this.toString.prettyPrintln(render)
    }

    override def toString: String = JsonUtils.serialize(this)

}

/**
 * HBase Qualifier
 *
 * @param cf     列族名称
 * @param col    列名称
 * @param `type` 列类型
 */
case class Qualifier(cf: String, col: String, `type`: String)

/**
 * HBase表
 *
 * @param namespace 命名空间
 * @param name      表名
 */
case class HBaseTable(namespace: String, name: String)