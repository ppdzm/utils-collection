package io.github.ppdzm.utils.office.excel.sheet

import jxl.format.{Alignment, VerticalAlignment}
import jxl.write._
import jxl.write.biff.CellValue

import scala.util.Try

case class JxlSheet(private val workbook: WritableWorkbook,
                    private val sheetName: String,
                    private val overwrite: scala.Boolean = false) {
    private val sheet: WritableSheet = if (workbook.getSheetNames.contains(sheetName)) {
        val s = workbook.getSheet(sheetName)
        if (overwrite) {
            val rowsCount = s.getRows
            for (rowIndex <- (0 until rowsCount).reverse)
                s.removeRow(rowIndex)
        }
        s
    }
    else
        workbook.createSheet(sheetName, workbook.getSheets.length)
    private var rowsCount: Int = sheet.getRows
    private var columns = List[String]()

    def writeColumnHeader(columns: List[String]): this.type = {
        if (columns.nonEmpty) {
            this.columns = columns
            if (rowsCount == 0 || overwrite) {
                for (colIndex <- columns.indices) {
                    val writableCellFormat = new WritableCellFormat()
                    // 设置水平居中
                    writableCellFormat.setAlignment(Alignment.CENTRE)
                    // 设置垂直居中
                    writableCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE)
                    // 设置自动换行
                    writableCellFormat.setWrap(true)
                    val columnCell = new Label(colIndex, 0, columns(colIndex), writableCellFormat)
                    sheet.addCell(columnCell)
                }
                rowsCount += 1
            }
        }
        this
    }

    def writeData(rows: List[List[Any]]): Unit = {
        for (rowIndex <- rows.indices; rowData = rows(rowIndex)) {
            for (colIndex <- rowData.indices) {
                this.writeCell(rowIndex + rowsCount, colIndex, rowData(colIndex), Try(columns(colIndex)).getOrElse(""))
            }
        }
    }

    def writeCell(row: Int, col: Int, value: Any, columnName: String = ""): Unit = {
        if (value != null) {
            val cell = getCell(col, row, columnName, value.toString)
            this.sheet.addCell(cell)
        }
    }

    def getCell(col: Int, row: Int, colName: String, value: String): CellValue = {
        if (value == null)
            new Label(col, row, "")
        else if (value.contains(".") && Try(value.toDouble).isSuccess) {
            val numberFormat = if (colName.endsWith("率") || colName.endsWith("比"))
                NumberFormats.PERCENT_FLOAT
            else
                NumberFormats.FLOAT
            val writableCellFormat = new WritableCellFormat(numberFormat)
            if (value.toDouble == value.toDouble.toInt)
                new jxl.write.Number(col, row, value.toDouble.toInt)
            else
                new jxl.write.Number(col, row, value.toDouble, writableCellFormat)
        }
        else if (Try(value.toInt).isSuccess)
            new jxl.write.Number(col, row, value.toInt)
        else
            new Label(col, row, value)
    }

}
