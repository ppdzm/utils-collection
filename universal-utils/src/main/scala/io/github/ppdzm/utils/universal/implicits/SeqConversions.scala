package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.base.Symbols._
import io.github.ppdzm.utils.universal.cli.PrettyBricks
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

import scala.reflect.ClassTag

object SeqConversions {

    implicit class SeqImplicits[T: ClassTag](seq: Seq[T]) {

        /**
         * 每个成员前面加上行号
         *
         * @return
         */
        def withRowNumber: Map[Int, T] = seq.indices.map(i => (i + 1) -> seq(i)).toMap

        /**
         * 和另外一个Seq进行笛卡尔积
         *
         * @param another 另一个Seq
         * @tparam A Seq成员类型
         * @return
         */
        def join[A](another: Seq[A]): Seq[(T, A)] = seq.flatMap(a => another.map((a, _)))

    }

    implicit class SeqSeqImplicits[T](seq: Seq[Seq[T]]) {
        private lazy val paddingChar = ' '
        private lazy val columns = if (seq.isEmpty) List("_col0") else (0 until seq.map(_.length).max).map("_col" + _)

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
            val (header, rows, footer) = this.toTable(alignment, explode, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
            if (header.nonEmpty)
                header.prettyPrintln(render)
            if (rows.nonEmpty) {
                rows.mkString(lineSeparator).prettyPrintln(render)
                if (footer.nonEmpty)
                    footer.prettyPrintln(render)
            }
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
            if (explode)
                this.toExplodedConsoleTable(alignment, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
            else
                this.toConsoleTable(alignment, length, linefeed, flank, null2Empty, transverse, truncate, truncateLength, vertical, columns)
        }

        /**
         * 处理带换行的值，比如show create table的返回值
         *
         * @param alignment      对齐方式
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
        def toExplodedConsoleTable(alignment: Any, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean, columns: Seq[String] = this.columns): (String, Seq[String], String) = {
            val displayColumns = getDisplayColumns(columns)
            val rowFlankBorder = PrettyBricks.rowFlankBorder(flank)
            val rowVerticalBorder = PrettyBricks.rowVerticalBorder(vertical)
            val takeNumber = if (length <= 0 || length >= seq.length) seq.length else length
            // 加工后实际展示的数据
            val displayArray = seq.map(_.map(_.n2e(null2Empty).trim.trim(lineFeed)))
                .map(a => a.map(e => if (truncate && e.length > truncateLength) e.take(truncateLength - 3) + "..." else e))
                .map(a => a.map(e => if (linefeed > 0) e.sliceByWidth(linefeed).map(_ + lineSeparator).mkString else e))
                .take(takeNumber)
            val withHeaderArray = displayArray
                .map(e => e.++(List.fill(displayColumns.length - e.length)("")))
                .:+(displayColumns)
                .map(_.map(_.split(lineSeparator)))
            // 每一列的最大宽度
            val maxWidthArray = displayColumns.indices.map(i => withHeaderArray.map(_ (i)).map(_.map(_.width).max).max)
            // 行数据
            val rows =
                displayArray
                    .map {
                        seq => seq.++(List.fill(displayColumns.length - seq.length)(this.paddingChar.toString))
                    }
                    .map {
                        seq =>
                            val maxRowsCount = seq.map(_.split(lineSeparator).length).max
                            val explodedRow =
                                seq.indices
                                    .map {
                                        i =>
                                            val paddingRowsCount = maxRowsCount - seq(i).split(lineSeparator).length
                                            val explodedColumn = seq(i).split(lineSeparator).map(e => e.pad(maxWidthArray(i) - e.width + e.length, this.paddingChar, alignment))
                                            Array.fill(paddingRowsCount / 2)("".pad(maxWidthArray(i), this.paddingChar, alignment))
                                                .++(explodedColumn)
                                                .++(Array.fill(paddingRowsCount / 2 + paddingRowsCount % 2)("".pad(maxWidthArray(i), this.paddingChar, alignment)))
                                    }
                            (0 until maxRowsCount).map(i => explodedRow.map(_ (i)).mkString(rowFlankBorder, rowVerticalBorder, rowFlankBorder)).mkString(lineSeparator)
                    }
            generateTable(rows, displayColumns, maxWidthArray, alignment, flank, transverse, vertical)
        }

        /**
         * 转换字符串表示的控制台表格
         *
         * @param alignment      对齐方式
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
        def toConsoleTable(alignment: Any, length: Int, linefeed: Int, flank: Boolean, null2Empty: Boolean, transverse: Boolean, truncate: Boolean, truncateLength: Int, vertical: Boolean, columns: Seq[String] = this.columns): (String, Seq[String], String) = {
            val displayColumns = getDisplayColumns(columns)
            val rowFlankBorder = PrettyBricks.rowFlankBorder(flank)
            val rowVerticalBorder = PrettyBricks.rowVerticalBorder(vertical)
            val takeNumber = if (length <= 0 || length >= seq.length) seq.length else length
            // 加工后实际展示的数据
            val displayedArray = seq.map(_.map(_.n2e(null2Empty).trim.trim(lineFeed)))
                .map(a => a.map(e => if (truncate && e.length > truncateLength) e.take(truncateLength - 3) + "..." else e))
                .take(takeNumber)
            val withEmptyColumnArray = displayedArray.map(e => e.++(List.fill(displayColumns.length - e.length)(" ")))
            val maxWidthArray = displayColumns.indices.map(i => withEmptyColumnArray.:+(displayColumns).map(_ (i)).map(_.width).max)
            val rows = withEmptyColumnArray
                .map {
                    seq =>
                        seq.indices
                            .map(i => seq(i).pad(maxWidthArray(i) - seq(i).width + seq(i).length, this.paddingChar, alignment))
                            .mkString(rowFlankBorder, rowVerticalBorder, rowFlankBorder)
                }
            generateTable(rows, displayColumns, maxWidthArray, alignment, flank, transverse, vertical)
        }

        /**
         * 转JSON
         *
         * @param columns 字段列表
         * @param pretty  是否进行美化
         */
        def toJson(columns: Seq[String], pretty: Boolean): Seq[String] = {
            seq.map {
                e =>
                    columns.indices.map {
                        i =>
                            val value = e(i).toString.trim
                            if (value.matches("\\{.+\\}")) {
                                val pairs = value.substring(1, value.length - 1).split(",").map(_.trim)
                                if (pairs.forall(_.contains("=")))
                                    columns(i) -> pairs.map(_.split("=")).map(_.:+("")).map(e => e(0) -> e(1)).toMap
                                else
                                    columns(i) -> value
                            }
                            else
                                columns(i) -> value
                    }.toMap
            }.map { e => JsonUtils.serialize(e, pretty) }
        }

        /**
         * D转Html
         *
         * @param columns 字段列表
         * @return
         */
        def toHtmlTable(columns: Seq[String]): String = {
            val header = columns.mkString("<tr><th>", "</th><th>", "</th></tr>")
            val rows = seq.map(_.mkString("<tr><td>", "</td><td>", "</td></tr>")).mkString(lineSeparator)
            s"""<table border=1 cellspacing=0>\n$header$rows"""
        }

        /**
         * 垂直显示
         *
         * @param columns 字段列表
         */
        def toVertical(columns: Seq[String]): Seq[String] = {
            if (seq.nonEmpty) {
                val leftMaxLength = columns.map(_.width).max
                val rowMaxWidth = seq.flatMap(_.map(_.toString.width)).max + leftMaxLength + 4
                val rowSeparator = if (rowMaxWidth > 100) "-" * 100
                else
                    "-" * rowMaxWidth
                val rows =
                    seq.map { e =>
                        columns.indices
                            .map(i => columns(i).pad(leftMaxLength, this.paddingChar, -1) + "\t" + e(i).toString)
                            .mkString(lineSeparator)
                    }
                rows.indices
                    .map {
                        i =>
                            if (i < rows.length - 1)
                                rowSeparator + lineSeparator + rows(i)
                            else
                                rowSeparator + lineSeparator + rows(i) + lineSeparator + rowSeparator
                    }
            } else {
                Seq[String]()
            }
        }

        /**
         * 转为属性表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         * @param columns    字段列表（即属性名称列表）
         */
        def toXMLWithAttributes(collection: String, member: String, columns: Seq[String]): (String, Seq[String], String) = {
            (s"<$collection>",
                seq.map(e => columns.indices.map(i => s"${columns(i)}=${e(i).toString.quote}").mkString(" ")).map(s"\t<$member " + _ + "/>"),
                s"<$collection/>")
        }

        /**
         * 转为元素表述的XML
         *
         * @param collection 根节点名称
         * @param member     第一层叶子节点名称
         * @param columns    字段列表（即第二层叶子节点名称列表）
         */
        def toXMLWithElements(collection: String, member: String, columns: Seq[String]): (String, Seq[String], String) = {
            (s"<$collection>",
                seq.map(e => columns.indices.map(i => s"<${columns(i)}>${e(i).toString.quote}</${columns(i)}>")
                    .mkString(s"\t<$member>\n\t\t", s"\n\t\t", s"\n\t</$member>")),
                s"<$collection/>")
        }

        /**
         * 生成二维表列头、行集合、表格下边框
         *
         * @param rows           数据
         * @param displayColumns 显示的列
         * @param maxWidthArray  每一列的最大宽度
         * @param alignment      对齐方式
         * @param flank          是否显示侧边框线
         * @param transverse     是否显示横向框线
         * @param vertical       是否显示垂直框线
         * @return
         */
        private def generateTable(rows: Seq[String], displayColumns: Seq[String], maxWidthArray: Seq[Int], alignment: Any, flank: Boolean, transverse: Boolean, vertical: Boolean): (String, Seq[String], String) = {
            // 列标题上边界
            val headerUpBorder = generateHeaderUpBorder(maxWidthArray, flank, vertical)
            // 列标题
            val columnHeader = generateColumnHeader(displayColumns, maxWidthArray, alignment, flank, vertical)
            // 列标题下边界
            val headerDownBorder = generateHeaderDownBorder(maxWidthArray, flank, vertical)
            // 行边界——横栏
            val rowBorder = generateRowBorder(maxWidthArray, flank, vertical)
            // 尾边界
            val tailBorder = generateTailBorder(maxWidthArray, flank, vertical)
            if (transverse) {
                // 行与行之间需要横栏时，将数据行除最后一行外，都加上横栏（最后一行的横栏默认始终存在）
                (
                    List(headerUpBorder, columnHeader, headerDownBorder).mkString(lineSeparator),
                    rows.mkString(lineSeparator + rowBorder + lineSeparator).split(lineSeparator).filter(_.nonEmpty).toSeq,
                    tailBorder
                )
            }
            else {
                (List(columnHeader, headerDownBorder).mkString(lineSeparator), rows, tailBorder)
            }
        }

        /**
         * 获得最终展示的列标题
         *
         * @param columns 字段列表
         * @return
         */
        private def getDisplayColumns(columns: Seq[String]): Seq[String] = {
            if (columns.isNull || columns.isEmpty)
                this.columns
            else if (seq.isEmpty)
                columns
            else if (columns.lengthCompare(seq.map(_.length).max) > 0)
                columns
            else
                columns.++((0 until seq.map(_.length).max - columns.length).map("_col" + _))
        }

        /**
         * 生成列标题上边界
         *
         * @param maxWidthArray 每一列元素的最大长度
         * @param flank         是否显示侧边框线
         * @param vertical      是否显示垂直框线
         * @return
         */
        private def generateHeaderUpBorder(maxWidthArray: Seq[Int], flank: Boolean, vertical: Boolean): String = {
            val headerLineBorder = PrettyBricks.headerLineBorder
            val headerUpBorderCross = PrettyBricks.headerUpBorderCross(vertical)
            if (flank)
                maxWidthArray.map(e => headerLineBorder * e).mkString(PrettyBricks.headerLeftTopAngle, headerUpBorderCross, PrettyBricks.headerRightTopAngle)
            else
                maxWidthArray.map(e => headerLineBorder * e).mkString(headerUpBorderCross)
        }

        /**
         * 生成列标题
         *
         * @param maxWidthArray  每一列元素的最大长度
         * @param displayColumns 最终展示的列标题
         * @param alignment      对齐方式
         * @param flank          是否显示侧边框线
         * @param vertical       是否显示垂直框线
         * @return
         */
        private def generateColumnHeader(displayColumns: Seq[String], maxWidthArray: Seq[Int], alignment: Any, flank: Boolean, vertical: Boolean): String = {
            val headerFlankBorder = PrettyBricks.headerFlankBorder(flank)
            val headerVerticalBorder = PrettyBricks.headerVerticalBorder(vertical)
            displayColumns
                .map(e => e.pad(maxWidthArray(displayColumns.indexOf(e)) - e.width + e.length, this.paddingChar, alignment))
                .mkString(headerFlankBorder, headerVerticalBorder, headerFlankBorder)
        }

        /**
         * 生成列标题下边界
         *
         * @param maxWidthArray 每一列元素的最大长度
         * @param flank         是否显示侧边框线
         * @param vertical      是否显示垂直框线
         * @return
         */
        private def generateHeaderDownBorder(maxWidthArray: Seq[Int], flank: Boolean, vertical: Boolean): String = {
            val headerLineBorder = PrettyBricks.headerLineBorder
            val headerDownBorderCross = PrettyBricks.headerDownBorderCross(vertical, seq.isEmpty)
            if (flank) {
                if (seq.nonEmpty)
                    maxWidthArray.map(e => headerLineBorder * e).mkString(PrettyBricks.headerRowLeftT, headerDownBorderCross, PrettyBricks.headerRowRightT)
                else
                    maxWidthArray.map(e => headerLineBorder * e).mkString(PrettyBricks.headerLeftBottomAngle, headerDownBorderCross, PrettyBricks.headerRightBottomAngle)
            }
            else
                maxWidthArray.map(e => headerLineBorder * e).mkString(headerDownBorderCross)
        }

        /**
         * 生成行分隔线
         *
         * @param maxWidthArray 每一列元素的最大长度
         * @param flank         是否显示侧边框线
         * @param vertical      是否显示垂直框线
         * @return
         */
        private def generateRowBorder(maxWidthArray: Seq[Int], flank: Boolean, vertical: Boolean): String = {
            val rowLineBorder = PrettyBricks.rowLineBorder
            val rowCrossBorder = PrettyBricks.rowCrossBorder(vertical)
            if (flank)
                maxWidthArray.map(e => rowLineBorder * e).mkString(PrettyBricks.rowLeftT, rowCrossBorder, PrettyBricks.rowRightT)
            else
                maxWidthArray.map(e => rowLineBorder * e).mkString(rowCrossBorder)
        }

        /**
         * 生成表尾
         *
         * @param maxWidthArray 每一列元素的最大长度
         * @param flank         是否显示侧边框线
         * @param vertical      是否显示垂直框线
         * @return
         */
        private def generateTailBorder(maxWidthArray: Seq[Int], flank: Boolean, vertical: Boolean): String = {
            val rowLineBorder = PrettyBricks.rowLineBorder
            val rowTailCrossBorder = PrettyBricks.rowTailCrossBorder(vertical)
            if (seq.nonEmpty) {
                if (flank)
                    maxWidthArray.map(e => rowLineBorder * e).mkString(PrettyBricks.rowLeftBottomAngle, rowTailCrossBorder, PrettyBricks.rowRightBottomAngle)
                else
                    maxWidthArray.map(e => rowLineBorder * e).mkString(rowTailCrossBorder)
            } else {
                ""
            }
        }

    }

    implicit class SeqComparableImplicits[T <: Comparable[T]](seq: Seq[T]) {

        /**
         * 升序
         *
         * @return
         */
        def ascending: Seq[T] = seq.sortWith((a, b) => a.compareTo(b) <= 0)

        /**
         * 降序
         *
         * @return
         */
        def descending: Seq[T] = seq.sortWith((a, b) => a.compareTo(b) > 0)

    }

}
