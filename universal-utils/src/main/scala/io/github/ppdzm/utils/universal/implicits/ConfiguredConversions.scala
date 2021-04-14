package io.github.ppdzm.utils.universal.implicits

import java.sql.ResultSet

import io.github.ppdzm.utils.universal.cli.{CliUtils, PrintConfig, Renders}
import io.github.ppdzm.utils.universal.config.Config

/**
 * Created by Stuart Alex on 2021/4/8.
 */
class ConfiguredConversions(implicit val config: Config) extends PrintConfig {

    implicit class CArrayArrayImplicits[T](array: Array[Array[T]]) {

        import ArrayConversions._

        private lazy val columns = (0 until array.map(_.length).max).map("col" + _).toArray

        /**
         * 打印显示字符串二维表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param columns   显示的列标题
         * @param alignment 对齐方式
         */
        def prettyShow(length: Int = length, truncate: Boolean = truncate, columns: Seq[String] = this.columns, alignment: Any = alignment): Unit = {
            array.prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转换为表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param columns   字段名称
         * @param alignment 对齐方式
         * @param explode   拆分长文本和换行符
         * @return
         */
        def toTable(length: Int = length, truncate: Boolean = truncate, columns: Seq[String] = this.columns, alignment: Any = alignment, explode: Boolean = explode): (String, Seq[String], String) = {
            array.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转为JSON
         *
         * @param columns 字段列表
         */
        def toJson(columns: Array[String]): Seq[String] = array.toJson(columns, pretty)

    }

    implicit class CSeqSeqImplicits[T](seq: Seq[Seq[T]]) {
        private lazy val columns = if (seq.isEmpty) List("_col0") else (0 until seq.map(_.length).max).map("_col" + _)

        import SeqConversions._

        /**
         * 打印显示字符串二维表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param columns   显示的列标题
         * @param alignment 对齐方式
         */
        def prettyShow(length: Int = length, truncate: Boolean = truncate, columns: Seq[String] = this.columns, alignment: Any = alignment): Unit = {
            seq.prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转换为表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param columns   字段名称
         * @param alignment 对齐方式
         * @param explode   拆分长文本和换行符
         * @return
         */
        def toTable(length: Int = length, truncate: Boolean = truncate, columns: Seq[String] = this.columns, alignment: Any = alignment, explode: Boolean = explode): (String, Seq[String], String) = {
            seq.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转为JSON
         *
         * @param columns 字段列表
         */
        def toJson(columns: Seq[String]): Seq[String] = seq.toJson(columns, pretty)

    }

    implicit class CAnyImplicits(any: Any) {

        /**
         * 渲染后输出
         */
        def prettyPrintln(): Unit = any.toString.prettyPrintln()

    }

    implicit class CStringImplicits(string: String) {

        /**
         * 填充指定字符到指定从长度
         *
         * @param alignment 原字符串在结果中的对齐位置
         * @param length    填充后的长度
         * @param char      被填充的字符
         * @return
         */
        def pad(length: Int, char: Char, alignment: Any): String = string.pad(length, char, alignment)

        /**
         * 渲染后输出
         */
        def prettyPrint(): Unit = print(string.rendering())

        /**
         * 渲染后输出
         */
        def prettyPrintln(): Unit = println(string.rendering())

        /**
         * 对文本进行颜色渲染
         *
         * @param render 渲染参数
         * @return
         */
        def rendering(render: String = render): String = CliUtils.render(string, render.split(";").map(_.toInt).map(Renders.valueOf): _*)

    }

    implicit class CResultSetImplicits(resultSet: ResultSet) {

        import ResultSetConversions._

        /**
         * 显示ResultSet的数据
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于this.defaultNumber时是否省略显示
         * @param alignment 对齐方式
         */
        def prettyShow(length: Int = length, truncate: Boolean = truncate, alignment: Any = alignment): Unit =
            resultSet.prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical)

        /**
         * 转换为表格
         *
         * @param length    显示的行数
         * @param truncate  数据长度大于20时是否省略显示
         * @param alignment 对齐方式
         * @param explode   拆分长文本和换行符
         * @return
         */
        def toTable(length: Int = length, truncate: Boolean = truncate, alignment: Any = alignment, explode: Boolean = explode): (String, Seq[String], String) = {
            resultSet.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical)
        }

        /**
         * DataFrame转Html
         *
         * @return
         */
        def toHtmlTable: String = resultSet.toHtmlTable(null2Empty)

        /**
         * 转为JSON
         *
         */
        def toJson: Seq[String] = resultSet.toJson(null2Empty, pretty)

        /**
         * 垂直显示
         *
         */
        def toVertical: Seq[String] = resultSet.toVertical(null2Empty)

        /**
         * 转为属性表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         */
        def toXMLWithAttributes(collection: String, member: String): (String, Seq[String], String) = resultSet.toXMLWithAttributes(collection, member, null2Empty)

        /**
         * 转为元素表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         */
        def toXMLWithElements(collection: String, member: String): (String, Seq[String], String) = resultSet.toXMLWithElements(collection, member, null2Empty)

    }

}
