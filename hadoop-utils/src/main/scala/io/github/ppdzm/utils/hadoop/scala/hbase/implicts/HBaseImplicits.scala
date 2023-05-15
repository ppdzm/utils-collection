package io.github.ppdzm.utils.hadoop.scala.hbase.implicts

import io.github.ppdzm.utils.hadoop.scala.hbase.{HBaseRow, Qualifier}
import io.github.ppdzm.utils.universal.base.Symbols.lineSeparator
import io.github.ppdzm.utils.universal.cli.table.PrettyBorder
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable

/**
 * Created by Stuart Alex on 2017/8/9.
 */
object HBaseImplicits {
    val border = new PrettyBorder()

    implicit class HBaseRowImplicits(hBaseRow: HBaseRow) {

        private val paddingChar = ' '

        /**
         * intersect 交接处字符
         * headerBorder 列标题行线条字符
         * rowBorder 数据行线条字符
         * flankBorder 数据行首尾字符
         * verticalBorder 数据项分隔字符
         */
        private val (intersect, headerBorder, rowBorder, flankBorder, verticalBorder) = ("+", "=", "-", "|", "|")

        def prettyShow(render: String, alignment: Any, linefeed: Int): Int = {
            val rowKey = hBaseRow.rowKey
            //实际要打印输出的数据
            val data = if (linefeed == 0)
                hBaseRow.data
            else
                hBaseRow.data.map(f => {
                    val family = f._1.sliceByWidth(linefeed).mkString(lineSeparator)
                    val qualifiers = f._2.map(q => {
                        val qualifier = q._1.sliceByWidth(linefeed).mkString(lineSeparator)
                        val timestamp = q._2._1
                        val value = q._2._2.sliceByWidth(linefeed).mkString(lineSeparator)
                        qualifier -> (timestamp, value)
                    })
                    (family, qualifiers)
                })
            val timestampLength = 13
            //最大列族宽度
            val maxFamilyWidth = data.keys.toArray.+:("Family").map(_.width).max
            //最大列名宽度
            val maxQualifierWidth = data.flatMap(_._2.keys).toArray.+:("Qualifier").map(_.width).max
            //最大列值宽度
            val maxValueWidth = if (linefeed == 0)
                data.flatMap(_._2.flatMap(_._2._2.split(lineSeparator))).toArray.+:("Value").map(_.width).max
            else
                Array(data.flatMap(_._2.flatMap(_._2._2.split(lineSeparator))).toArray.+:("Value").map(_.width).max, linefeed).min
            //表格除左右最外边框的宽度
            val width = Seq(maxFamilyWidth + maxQualifierWidth + maxValueWidth + timestampLength + 3, rowKey.width).max
            //值单元格实际宽度
            val valueCellWidth = width - 3 - maxFamilyWidth - maxQualifierWidth - timestampLength
            //1. 首行上边框（包括角）
            val headerTopBorder = border.headerLeftTopAngle + border.headerHorizontal * width + border.headerRightTopAngle
            //2. 首行（row key所在行）
            val header = border.headerVertical + rowKey.pad(width - rowKey.width + rowKey.length, this.paddingChar, alignment) + border.headerVertical
            //3. 首行下边框（除首尾部角，还有中间的T型交叉）
            val headerBottomBorder = Seq(maxFamilyWidth, maxQualifierWidth, valueCellWidth, timestampLength)
              .map(border.headerHorizontal * _).mkString(border.headerLeftT, border.headerUpT, border.headerRightT)
            //4. 列标题
            val columnsTitle = Map("Family" -> maxFamilyWidth, "Qualifier" -> maxQualifierWidth, "Value" -> valueCellWidth, "Timestamp" -> timestampLength)
              .map(e => e._1.pad(e._2, this.paddingChar, alignment)).mkString(border.headerVertical, border.headerVertical, border.headerVertical)
            //5. 字段下边框
            val familyBottomBorder = Seq(maxFamilyWidth, maxQualifierWidth, valueCellWidth, timestampLength)
              .map(border.headerHorizontal * _).mkString(border.headerRowLeftT, border.headerRowCross, border.headerRowRightT)
            //n-1. 表格最下面的边框
            val rowBottomBorder = Seq(maxFamilyWidth, maxQualifierWidth, valueCellWidth, timestampLength)
              .map(border.rowHorizontal * _).mkString(border.rowLeftBottomAngle, border.rowDownT, border.rowRightBottomAngle)
            //列与列之间的分隔边框
            val qualifierRowBorder = lineSeparator + Seq(maxQualifierWidth, valueCellWidth, timestampLength)
              .map(border.rowHorizontal * _).mkString(border.rowLeftT, border.rowCross, border.rowRightT)
            val rows: String = data
              .map {
                  f =>
                      val qualifierCells =
                          f._2.map {
                              q =>
                                  //列值
                                  val value = q._2._2.split(lineSeparator).map(e => e.pad(valueCellWidth - e.width + e.length, this.paddingChar, alignment))
                                  val paddingNumber = value.length - q._1.split(lineSeparator).length
                                  //列族
                                  val qualifier = Array.fill(paddingNumber / 2)(this.paddingChar.toString * maxQualifierWidth)
                                    .++(q._1.split(lineSeparator).map(_.pad(maxQualifierWidth, this.paddingChar, alignment)))
                                    .++(Array.fill(paddingNumber / 2 + paddingNumber % 2)(this.paddingChar.toString * maxQualifierWidth))
                                  //时间戳
                                  val timestamp = Array.fill(paddingNumber / 2)(this.paddingChar.toString * timestampLength)
                                    .++(Array(q._2._1.toString).map(_.pad(timestampLength, this.paddingChar, alignment)))
                                    .++(Array.fill(paddingNumber / 2 + paddingNumber % 2)(this.paddingChar.toString * timestampLength))
                                  val qvts = qualifier.indices.map(i => Array(qualifier(i), value(i), timestamp(i)).mkString(border.rowVertical, border.rowVertical, border.rowVertical))
                                  qvts.mkString(lineSeparator)
                          }
                            .mkString("", qualifierRowBorder + lineSeparator, "").split(lineSeparator)
                      val familyPaddingNumber = qualifierCells.length - f._1.split(lineSeparator).length
                      val familyCells = Array.fill(familyPaddingNumber / 2)(this.paddingChar.toString * maxFamilyWidth)
                        .++(f._1.split(lineSeparator).map(_.pad(maxFamilyWidth, this.paddingChar, alignment)))
                        .++(Array.fill(familyPaddingNumber / 2 + familyPaddingNumber % 2)(this.paddingChar.toString * maxFamilyWidth))
                      familyCells.indices.map(i => border.rowVertical + familyCells(i) + qualifierCells(i)).mkString(lineSeparator)
              }
              .mkString("", lineSeparator + familyBottomBorder + lineSeparator, lineSeparator + rowBottomBorder)
            headerTopBorder.prettyPrintln(render)
            header.prettyPrintln(render)
            headerBottomBorder.prettyPrintln(render)
            columnsTitle.prettyPrintln(render)
            familyBottomBorder.prettyPrintln(render)
            rows.prettyPrintln(render)
            rows.split("\n").length + 5
        }

        @deprecated
        private def prettyShowV1(render: String, alignment: Any, linefeed: Int): Int = {
            val rowKey = hBaseRow.rowKey
            //实际要打印输出的数据
            val data = if (linefeed == 0)
                hBaseRow.data
            else
                hBaseRow.data.map(f => {
                    val family = f._1.sliceByWidth(linefeed).mkString(lineSeparator)
                    val qualifiers = f._2.map(q => {
                        val qualifier = q._1.sliceByWidth(linefeed).mkString(lineSeparator)
                        val timestamp = q._2._1
                        val value = q._2._2.sliceByWidth(linefeed).mkString(lineSeparator)
                        qualifier -> (timestamp, value)
                    })
                    (family, qualifiers)
                })
            val timestampLength = 13
            //最大列族宽度
            val maxFamilyWidth = data.keys.toArray.+:("Family").map(_.width).max
            //最大列名宽度
            val maxQualifierWidth = data.flatMap(_._2.keys).toArray.+:("Qualifier").map(_.width).max
            //最大列值宽度
            val maxValueWidth = if (linefeed == 0)
                data.flatMap(_._2.flatMap(_._2._2.split(lineSeparator))).toArray.+:("Value").map(_.width).max
            else
                Array(data.flatMap(_._2.flatMap(_._2._2.split(lineSeparator))).toArray.+:("Value").map(_.width).max, linefeed).min
            //表格除左右最外边框的宽度
            val width = Seq(maxFamilyWidth + maxQualifierWidth + maxValueWidth + timestampLength + 3, rowKey.width).max
            //首行上边框（包括角）
            val topHeaderBorder = this.intersect + this.headerBorder * width + this.intersect
            //首行
            val header = this.flankBorder + rowKey.pad(width - rowKey.width + rowKey.length, this.paddingChar, alignment) + this.flankBorder
            //值单元格实际宽度
            val valueCellWidth = width - 3 - maxFamilyWidth - maxQualifierWidth - timestampLength
            //列标题下边框（除首尾部角，还有中间的T型交叉）
            val bottomHeaderBorder = Seq(maxFamilyWidth, maxQualifierWidth, valueCellWidth, timestampLength)
              .map(this.headerBorder * _).mkString(this.intersect, this.intersect, this.intersect)
            //列标题
            val columnsTitle = Map("Family" -> maxFamilyWidth, "Qualifier" -> maxQualifierWidth, "Value" -> valueCellWidth, "Timestamp" -> timestampLength)
              .map(e => e._1.pad(e._2, this.paddingChar, alignment)).mkString(this.flankBorder, this.verticalBorder, this.flankBorder)
            //RowKey下边框、数据区域列族与列族之前的行分隔边框、表格最下面的边框
            val familyRowBorder = Seq(maxFamilyWidth, maxQualifierWidth, valueCellWidth, timestampLength)
              .map(this.rowBorder * _).mkString(this.intersect, this.intersect, this.intersect)
            //列与列之间的分隔边框
            val qualifierRowBorder = lineSeparator + Seq(maxQualifierWidth, valueCellWidth, timestampLength)
              .map(this.rowBorder * _).mkString(this.intersect, this.intersect, this.intersect)
            val rows = data.map(f => {
                val qualifierCells = f._2.map(q => {
                    //列值
                    val value = q._2._2.split(lineSeparator).map(e => e.pad(valueCellWidth - e.width + e.length, this.paddingChar, alignment))
                    val paddingNumber = value.length - q._1.split(lineSeparator).length
                    //列族
                    val qualifier = Array.fill(paddingNumber / 2)(this.paddingChar.toString * maxQualifierWidth)
                      .++(q._1.split(lineSeparator).map(_.pad(maxQualifierWidth, this.paddingChar, alignment)))
                      .++(Array.fill(paddingNumber / 2 + paddingNumber % 2)(this.paddingChar.toString * maxQualifierWidth))
                    //时间戳
                    val timestamp = Array.fill(paddingNumber / 2)(this.paddingChar.toString * timestampLength)
                      .++(Array(q._2._1.toString).map(_.pad(timestampLength, this.paddingChar, alignment)))
                      .++(Array.fill(paddingNumber / 2 + paddingNumber % 2)(this.paddingChar.toString * timestampLength))
                    val qvts = qualifier.indices.map(i => Array(qualifier(i), value(i), timestamp(i)).mkString(this.verticalBorder, this.verticalBorder, this.flankBorder))
                    qvts.mkString(lineSeparator)
                }).mkString("", qualifierRowBorder + lineSeparator, "").split(lineSeparator)
                val familyPaddingNumber = qualifierCells.length - f._1.split(lineSeparator).length
                val familyCells = Array.fill(familyPaddingNumber / 2)(this.paddingChar.toString * maxFamilyWidth)
                  .++(f._1.split(lineSeparator).map(_.pad(maxFamilyWidth, this.paddingChar, alignment)))
                  .++(Array.fill(familyPaddingNumber / 2 + familyPaddingNumber % 2)(this.paddingChar.toString * maxFamilyWidth))
                familyCells.indices.map(i => this.flankBorder + familyCells(i) + qualifierCells(i)).mkString(lineSeparator)
            }).mkString("", lineSeparator + familyRowBorder + lineSeparator, lineSeparator + familyRowBorder)
            topHeaderBorder.prettyPrintln(render)
            header.prettyPrintln(render)
            bottomHeaderBorder.prettyPrintln(render)
            columnsTitle.prettyPrintln(render)
            bottomHeaderBorder.prettyPrintln(render)
            rows.prettyPrintln(render)
            rows.split("\n").length + 5
        }

    }

    implicit class HBaseResultImplicits(result: Result) {

        def prettyShow(render: String, alignment: Any, linefeed: Int): Int = HBaseRow(result).prettyShow(render, alignment, linefeed)

        def toMap: Map[String, String] = {
            val map: mutable.Map[String, String] = mutable.Map()
            val scanner = result.cellScanner()
            while (scanner.advance) {
                val cell = scanner.current
                val column = new String(CellUtil.cloneQualifier(cell))
                val value = new String(CellUtil.cloneValue(cell))
                map.put(column, value)
            }
            map.toMap
        }

        def tuple1(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            if (hasRowKey)
                Tuple1(tuple)
            else
                Tuple1(tuple0)
        }

        def tuple2(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            if (hasRowKey)
                (tuple, tuple0)
            else
                (tuple0, tuple1)
        }

        /**
         * 获取HBase一行数据中指定单元格的数据
         *
         * @param qualifier io.github.ppdzm.universal.bigdata.spark.utils.hadoop.hbase.Qualifier
         * @return
         */
        def getCellValue(qualifier: Qualifier): String = {
            val cell = result.getColumnLatestCell(qualifier.cf.getBytes, qualifier.col.getBytes())
            if (cell == null)
                null
            else
                Bytes.toString(CellUtil.cloneValue(cell))
        }

        def tuple3(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            if (hasRowKey)
                (tuple, tuple0, tuple1)
            else
                (tuple0, tuple1, tuple2)
        }

        def tuple4(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2)
            else
                (tuple0, tuple1, tuple2, tuple3)
        }

        def tuple5(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4)
        }

        def tuple6(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5)
        }

        def tuple7(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6)
        }

        def tuple8(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7)
        }

        def tuple9(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8)
        }

        def tuple10(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9)
        }

        def tuple11(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10)
        }

        def tuple12(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11)
        }

        def tuple13(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12)
        }

        def tuple14(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13)
        }

        def tuple15(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14)
        }

        def tuple16(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15)
        }

        def tuple17(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16)
        }

        def tuple18(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            val tuple17 = this.getCellValue(columns(fields(17)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17)
        }

        def tuple19(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            val tuple17 = this.getCellValue(columns(fields(17)))
            val tuple18 = this.getCellValue(columns(fields(18)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18)
        }

        def tuple20(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            val tuple17 = this.getCellValue(columns(fields(17)))
            val tuple18 = this.getCellValue(columns(fields(18)))
            val tuple19 = this.getCellValue(columns(fields(19)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18, tuple19)
        }

        def tuple21(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            val tuple17 = this.getCellValue(columns(fields(17)))
            val tuple18 = this.getCellValue(columns(fields(18)))
            val tuple19 = this.getCellValue(columns(fields(19)))
            val tuple20 = this.getCellValue(columns(fields(20)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18, tuple19)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18, tuple19, tuple20)
        }

        def tuple22(fields: List[String], columns: Map[String, Qualifier], hasRowKey: Boolean) = {
            val tuple = Bytes.toString(result.getRow)
            val tuple0 = this.getCellValue(columns(fields.head))
            val tuple1 = this.getCellValue(columns(fields(1)))
            val tuple2 = this.getCellValue(columns(fields(2)))
            val tuple3 = this.getCellValue(columns(fields(3)))
            val tuple4 = this.getCellValue(columns(fields(4)))
            val tuple5 = this.getCellValue(columns(fields(5)))
            val tuple6 = this.getCellValue(columns(fields(6)))
            val tuple7 = this.getCellValue(columns(fields(7)))
            val tuple8 = this.getCellValue(columns(fields(8)))
            val tuple9 = this.getCellValue(columns(fields(9)))
            val tuple10 = this.getCellValue(columns(fields(10)))
            val tuple11 = this.getCellValue(columns(fields(11)))
            val tuple12 = this.getCellValue(columns(fields(12)))
            val tuple13 = this.getCellValue(columns(fields(13)))
            val tuple14 = this.getCellValue(columns(fields(14)))
            val tuple15 = this.getCellValue(columns(fields(15)))
            val tuple16 = this.getCellValue(columns(fields(16)))
            val tuple17 = this.getCellValue(columns(fields(17)))
            val tuple18 = this.getCellValue(columns(fields(18)))
            val tuple19 = this.getCellValue(columns(fields(19)))
            val tuple20 = this.getCellValue(columns(fields(20)))
            val tuple21 = this.getCellValue(columns(fields(21)))
            if (hasRowKey)
                (tuple, tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18, tuple19, tuple20)
            else
                (tuple0, tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7, tuple8, tuple9, tuple10, tuple11, tuple12, tuple13, tuple14, tuple15, tuple16, tuple17, tuple18, tuple19, tuple20, tuple21)
        }

    }

}