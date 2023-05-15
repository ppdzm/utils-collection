package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.SeqConversions._

import scala.reflect._

object ArrayConversions {

    implicit class ArrayImplicits[T: ClassTag](array: Array[T]) {

        /**
         * 每个成员前面加上行号
         *
         * @return
         */
        def withRowNumber: Map[Int, T] = array.indices.map(i => (i + 1) -> array(i)).toMap

        def join[A](another: Array[A]): Array[(T, A)] = array.flatMap(a => another.map((a, _)))

        /**
         * 组合
         *
         * @param number 组合内元素个数，-1表示所有组合
         * @return
         */
        def getCombination(number: Int = -1): List[List[T]] = {
            assert(array != null, "array cannot be null")
            assert(array.nonEmpty, "array cannot be empty")
            val elementNumber = array.length
            (0 until (1 << elementNumber))
              .map {
                  cursor =>
                      (0 until elementNumber)
                        .map(e => e -> (1 << e))
                        .filter(e => (e._2 & cursor) != 0)
                        .map(_._1)
                        .map(array(_))
                        .toList
              }
              .filter(number == -1 || _.length == number)
              .toList
        }

    }

    implicit class ArrayArrayImplicits[T](array: Array[Array[T]]) {
        private lazy val columns = (0 until array.map(_.length).max).map("col" + _).toArray

        /**
         * 打印显示字符串二维表格
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
         * @param columns        显示的列标题
         */
        def prettyShow(render: String, alignment: Any, explode: Boolean, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean, columns: Seq[String] = this.columns): Unit = {
            array.map(_.toList)
              .toList
              .prettyShow(render, alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 转换为表格
         *
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
         * @param columns        显示的列标题
         * @return
         */
        def toTable(alignment: Any, explode: Boolean, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean, columns: Seq[String] = this.columns): (String, Seq[String], String) = {
            array.map(_.toList)
              .toList
              .toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * DataFrame转Html
         *
         * @return
         */
        def toHtmlTable(columns: Array[String]): String = array.map(_.toList).toList.toHtmlTable(columns.toList)

        /**
         * 转为JSON
         *
         * @param columns 字段列表
         */
        def toJson(columns: Array[String], pretty: Boolean): Seq[String] = array.map(_.toList).toList.toJson(columns.toList, pretty)

        /**
         * 垂直显示
         *
         * @param columns 字段列表
         */
        def toVertical(columns: Array[String]): Seq[String] = array.map(_.toList).toList.toVertical(columns.toList)

        /**
         * 转为属性表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         * @param columns    字段列表（即属性名称列表）
         */
        def toXMLWithAttributes(collection: String, member: String, columns: Array[String]): (String, Seq[String], String) = {
            array.map(_.toList).toList.toXMLWithAttributes(collection, member, columns.toList)
        }

        /**
         * 转为元素表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         * @param columns    字段列表（即第二层叶子节点名称列表）
         */
        def toXMLWithElements(collection: String, member: String, columns: Array[String]): (String, Seq[String], String) = {
            array.map(_.toList).toList.toXMLWithElements(collection, member, columns.toList)
        }

    }

    implicit class ArrayComparableImplicits[T <: Comparable[T]](array: Array[T]) {

        /**
         * 升序
         *
         * @return
         */
        def ascending: Array[T] = array.sortWith((a, b) => a.compareTo(b) <= 0)

        /**
         * 降序
         *
         * @return
         */
        def descending: Array[T] = array.sortWith((a, b) => a.compareTo(b) > 0)

    }

    implicit class ArrayArrayComparableImplicits[T <: Comparable[T]](array: Array[Array[T]]) {
        /**
         * 计算交集
         *
         * @return
         */
        def intersect: Array[T] = {
            val head = array.head
            val tail = array.tail
            var result = head
            for (index <- tail.indices)
                result = result.intersect(tail(index))
            result.sorted
        }

    }

    implicit class TupleKVArrayImplicits[K, V](array: Array[(K, V)]) {

        /**
         * 将Key-Value-Pair转成String二元组
         *
         * @return
         */
        def withKeyValueToString: Array[(String, String)] = array.map { case (key, value) => key.toString -> value.toString }

    }

    implicit class Tuple2SArrayImplicits(array: Array[(String, String)]) {

        /**
         * 按键升序排序
         *
         * @return
         */
        def withKeySorted: Array[(String, String)] = array.sortBy(_._1)

        /**
         * 将每个键用空格填充至相同宽度后和value连在一起
         *
         * @param align  对齐方式
         * @param start  首部填充字符串
         * @param middle key value间填充字符串
         * @param end    尾部填充字符串
         * @return
         */
        def withKeyPadded(align: Any, start: String, middle: String, end: String): Array[String] = {
            val maxLengthOfKey = array.map(_._1).map(_.length).max
            array.map({ case (key, value) => start + key.pad(maxLengthOfKey, ' ', -1) + middle + value + end })
        }

    }

}