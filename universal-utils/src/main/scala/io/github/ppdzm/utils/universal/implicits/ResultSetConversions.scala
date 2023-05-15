package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.SeqConversions._

import java.sql.{ResultSet, ResultSetMetaData}
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

object ResultSetConversions {

    implicit class ResultSetImplicits(resultSet: ResultSet) {
        lazy val metadata: ResultSetMetaData = resultSet.getMetaData
        lazy val columns: Seq[String] = (1 to metadata.getColumnCount).map(i => metadata.getColumnName(i))
        lazy val (null2EmptyList, null2NullStringList) = {
            val buffer1 = ListBuffer[List[String]]()
            val buffer2 = ListBuffer[List[String]]()
            val count = metadata.getColumnCount
            while (resultSet.next()) {
                val row = (1 to count).map(i => resultSet.getString(i))
                buffer1 += row.map(_.n2e).toList
                buffer2 += row.map(_.n2e(false)).toList
            }
            (buffer1.toList, buffer2.toList)
        }

        def singleColumnList(column: String, null2Empty: Boolean): List[String] = {
            if (this.columns.contains(column)) {
                singleColumnList(this.columns.indexOf(column), null2Empty)
            } else {
                List[String]()
            }
        }

        def singleColumnList(columnIndex: Int, null2Empty: Boolean = true): List[String] = {
            if (null2Empty)
                this.null2EmptyList.map(_ (columnIndex))
            else
                this.null2NullStringList.map(_ (columnIndex))
        }

        def scalar[T: ClassTag](rowIndex: Int = 0, columnIndex: Int = 0, null2Empty: Boolean = true): T = {
            if (null2Empty)
                this.null2EmptyList(rowIndex)(columnIndex).asInstanceOf[T]
            else
                this.null2NullStringList(rowIndex)(columnIndex).asInstanceOf[T]
        }

        /**
         * 显示ResultSet的数据
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
         * @param explode   拆分长文本和换行符
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
        def toHtmlTable(null2Empty: Boolean): String = {
            if (null2Empty)
                this.null2EmptyList.toHtmlTable(columns)
            else
                this.null2NullStringList.toHtmlTable(columns)
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
