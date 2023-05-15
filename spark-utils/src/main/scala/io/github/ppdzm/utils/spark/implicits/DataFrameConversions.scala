package io.github.ppdzm.utils.spark.implicits

import io.github.ppdzm.utils.universal.base.Symbols._
import io.github.ppdzm.utils.universal.implicits.ArrayConversions.ArrayArrayImplicits
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._

object DataFrameConversions {

    implicit class DataFrameImplicits(dataFrame: DataFrame) {
        lazy val columns: Array[String] = dataFrame.columns
        lazy val (null2EmptyList, null2NullStringList) = {
            val rows = dataFrame.collect()
            (rows.map(e => columns.map(c => e.get(e.fieldIndex(c)).n2e(true))), rows.map(e => columns.map(c => e.get(e.fieldIndex(c)).n2e(false))))
        }
        lazy val size: Int = this.null2EmptyList.length

        /**
         * bool转int
         *
         * @return
         */
        def castBoolean2Int: DataFrame = {
            var df = dataFrame
            df.schema.foreach(sf => {
                sf.dataType.typeName match {
                    case "boolean" => df = df.withColumn(sf.name, df(sf.name).cast(IntegerType))
                    case _ =>
                }
            })
            df
        }

        /**
         * 根据《字段-类型》的映射修改字段类型
         *
         * @param mapping 《字段-类型》映射关系
         * @return
         */
        def cast(mapping: Map[String, String]): DataFrame = {
            var df = dataFrame
            mapping.foreach(m => {
                val column = m._1
                m._2.toLowerCase match {
                    case "bool" => df = df.withColumn(column, df(column).cast(BooleanType))
                    case "boolean" => df = df.withColumn(column, df(column).cast(BooleanType))
                    case "double" => df = df.withColumn(column, df(column).cast(DoubleType))
                    case "float" => df = df.withColumn(column, df(column).cast(FloatType))
                    case "int" => df = df.withColumn(column, df(column).cast(IntegerType))
                    case "integer" => df = df.withColumn(column, df(column).cast(IntegerType))
                    case "long" => df = df.withColumn(column, df(column).cast(LongType))
                    case "bigint" => df = df.withColumn(column, df(column).cast(LongType))
                    case other => if (other.startsWith("decimal"))
                        df = df.withColumn(column, df(column).cast(other))
                }
            })
            df
        }

        /**
         * 显示DataFrame的数据
         *
         * @param render         文本渲染器
         * @param alignment      对齐方式
         * @param explode        是否将文本中的换行符展开
         * @param length         显示的行数
         * @param linefeed       强制换行的字符宽度
         * @param flank          是否显示侧边框线
         * @param null2Empty     是否将null值以空字符串显示
         * @param transverse     是否显示横向框线
         * @param truncate       是否截断显示长字符串
         * @param truncateLength 字符串超过此长度时截断显示
         * @param vertical       是否以垂直 column name: column value的形式显示
         */
        def prettyShow(render: String, alignment: Any, explode: Boolean, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean): Unit = {
            if (null2Empty)
                this.null2EmptyList.prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
            else
                this.null2NullStringList.prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转换为表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param alignment 对齐方式
         * @return
         */
        def toTable(alignment: Any, explode: Boolean, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean): (String, Seq[String], String) = {
            if (null2Empty)
                this.null2EmptyList.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
            else
                this.null2NullStringList.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * DataFrame转Html
         *
         * @return
         */
        def toHtmlTable: String = {
            val header = dataFrame.schema.map(_.name).mkString("<tr><th>", "</th><th>", "</th></tr>")
            val rows = dataFrame.collect().map(_.mkString("<tr><td>", "</td><td>", "</td></tr>")).mkString(lineSeparator)
            s"""<table border=1 cellspacing=0>\n$header$rows"""
        }

        /**
         * 转为JSON
         *
         */
        def toJson(null2Empty: Boolean, pretty: Boolean): Seq[String] = {
            if (null2Empty)
                this.null2EmptyList.toJson(columns, pretty)
            else
                this.null2NullStringList.toJson(columns, pretty)
        }

        /**
         * 垂直显示
         *
         */
        def toVertical(null2Empty: Boolean): Seq[String] = {
            if (null2Empty)
                this.null2EmptyList.toVertical(columns)
            else
                this.null2NullStringList.toVertical(columns)
        }

        /**
         * 转为属性表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         */
        def toXMLWithAttributes(collection: String, member: String, null2Empty: Boolean): (String, Seq[String], String) = {
            if (null2Empty)
                this.null2EmptyList.toXMLWithAttributes(collection, member, this.columns)
            else
                this.null2NullStringList.toXMLWithAttributes(collection, member, this.columns)
        }

        /**
         * 转为元素表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         */
        def toXMLWithElements(collection: String, member: String, null2Empty: Boolean): (String, Seq[String], String) = {
            if (null2Empty)
                this.null2EmptyList.toXMLWithElements(collection, member, this.columns)
            else
                this.null2NullStringList.toXMLWithElements(collection, member, this.columns)
        }

    }

}
