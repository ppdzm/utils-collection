package io.github.ppdzm.utils.office.excel.scala.sheet

import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.ss.usermodel.CellType._
import org.apache.poi.ss.usermodel._

import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait PoiSheet {
    protected val workbook: Workbook
    protected val sheetName: String
    protected val overwrite: Boolean
    protected val sheet: Sheet = getSheet(overwrite)
    private val numericCellStyle = workbook.createCellStyle()
    private val percentageCellStyle = workbook.createCellStyle()
    numericCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"))
    private val textCellStyle = workbook.createCellStyle()
    percentageCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"))
    private var headers: List[String] = List[String]()
    textCellStyle.setWrapText(true)
    textCellStyle.setAlignment(HorizontalAlignment.CENTER)
    textCellStyle.setVerticalAlignment(VerticalAlignment.CENTER)

    /**
     * 获取sheet
     *
     * @param overwrite 复写
     * @return
     */
    def getSheet(overwrite: Boolean): Sheet = {
        if (workbook.getSheet(sheetName) != null) {
            val s = workbook.getSheet(sheetName)
            if (overwrite) {
                for (rowIndex <- 0 until s.getLastRowNum + 1; row = s.getRow(rowIndex) if row != null) {
                    s.removeRow(row)
                }
            }
            s
        }
        else
            workbook.createSheet(sheetName)
    }

    /**
     * 写入列标题
     *
     * @param headers 列标题
     * @return
     */
    def writeColumnHeader(headers: List[String]): this.type = {
        if (headers.nonEmpty) {
            this.headers = headers
            if (sheet.getLastRowNum < 1 || overwrite || headerChanged(headers)) {
                val headerRow = sheet.createRow(0)
                for (colIndex <- headers.indices) {
                    val headerCell = headerRow.createCell(colIndex, CellType.STRING)
                    headerCell.setCellStyle(textCellStyle)
                    headerCell.setCellValue(headers(colIndex))
                }
            }
        }
        this
    }

    /**
     * 列标题是否发生变化
     *
     * @param headers 列标题
     * @return
     */
    def headerChanged(headers: List[String]): Boolean = {
        sheet.getRow(0).getLastCellNum < headers.length ||
          headers.indices.exists {
              i => sheet.getRow(0).getCell(i).getStringCellValue != headers(i)
          }
    }

    /**
     * 写入数据
     *
     * @param rows 数据
     */
    def writeData(rows: List[List[Any]]): Unit = {
        val rowsCount = sheet.getLastRowNum + 1
        for (x <- rows.indices) {
            val rowData = rows(x)
            val excelRow = this.sheet.createRow(x + rowsCount)
            for (y <- rowData.indices) {
                if (rowData(y) != null && rowData(y).toString.toLowerCase != "null" && rowData(y).toString.toLowerCase != "nah")
                    this.writeCell(excelRow, y, rowData(y), Try(this.headers(y)).getOrElse(""))
            }
        }
    }

    /**
     * 向单个单元格写入数据
     *
     * @param excelRow   一行数据
     * @param colIndex   列号
     * @param rawValue   原始数据
     * @param columnName 列标题
     */
    def writeCell(excelRow: Row, colIndex: Int, rawValue: Any, columnName: String = ""): Unit = {
        val cell = excelRow.createCell(colIndex)
        if (rawValue == null)
            cell.setCellValue("")
        else {
            val value = rawValue.toString
            if (value.contains(".") && Try(value.toDouble).isSuccess) {
                if (columnName.endsWith("率") || columnName.endsWith("比")) {
                    cell.setCellValue(value.toDouble)
                    cell.setCellStyle(percentageCellStyle)
                } else if (value.toDouble != value.toDouble.toInt) {
                    cell.setCellValue(value.toDouble)
                    cell.setCellStyle(numericCellStyle)
                } else {
                    cell.setCellValue(value.toDouble.toInt)
                }
            } else if (Try(value.toInt).isSuccess)
                cell.setCellValue(value.toInt)
            else {
                cell.setCellValue(value)
                //cell.setCellStyle(textCellStyle)
            }
        }
    }

    def getRowValues(rowIndex: Int): List[String] = {
        val row = sheet.getRow(rowIndex)
        getRowValues(row)
    }

    def getRowValues(row: Row): List[String] = {
        if (row == null) {
            return null
        }
        (0 until row.getLastCellNum)
          .map {
              columnIndex =>
                  val cell = row.getCell(columnIndex)
                  getCellValue(cell)
          }
          .toList
    }

    def getCellValue(cell: Cell): String = {
        if (cell == null) {
            return null
        }
        if (cell.getCellTypeEnum == null) {
            return null
        }
        val cellValue = cell.getCellTypeEnum match {
            case NUMERIC | FORMULA => new java.math.BigDecimal(cell.getNumericCellValue).toPlainString
            case _ => cell.getStringCellValue
        }
        if (cellValue == null)
            return null
        cellValue.toString.trim
    }

    def read(header: Boolean, headerIndex: Int): (List[String], List[List[String]]) = {
        val headers = if (header) {
            getRowValues(headerIndex)
        } else {
            null
        }
        val sheetData = ListBuffer[List[String]]()
        for (rowIndex <- headerIndex + 1 until sheet.getLastRowNum) {
            val row = sheet.getRow(rowIndex)
            val rowData = getRowValues(row)
            sheetData += rowData
        }
        (headers, sheetData.toList)
    }

}
