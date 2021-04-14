package io.github.ppdzm.utils.office.excel.sheet

import java.text.DecimalFormat

import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import scala.util.Try

case class OOXMLSheet(workbook: XSSFWorkbook, sheetName: String, overwrite: Boolean = false) extends PoiSheet {
    protected lazy val firstDataRowIndex = 1
    protected lazy val lastDataRowIndex: Int = sheet.getLastRowNum
    override val sheet: XSSFSheet = getSheet(overwrite).asInstanceOf[XSSFSheet]

    def getColumnData(columnName: String): Array[String] = {
        getColumnData(locateColumn(columnName))
    }

    def locateColumn(columnName: String): Int = {
        val columnRow = sheet.getRow(0)
        val columnIndex = (0 until columnRow.getLastCellNum)
            .map(columnRow.getCell)
            .map(_.getStringCellValue.trim)
            .zipWithIndex
            .find(_._1 == columnName)
            .getOrElse(throw new Exception(s"column $columnName not found in sheet ${sheet.getSheetName}"))
            ._2
        columnIndex
    }

    def getColumnData(columnIndex: Int): Array[String] = {
        val decimalFormat = new DecimalFormat("0")
        Range(firstDataRowIndex, lastDataRowIndex)
            .map(sheet.getRow)
            .map(_.getCell(columnIndex))
            .filter(_ != null)
            .map {
                c =>
                    val value = Try(decimalFormat.format(c.getNumericCellValue))
                    if (value.isSuccess) {
                        if (value.get.toDouble == value.get.toInt) {
                            value.get.toInt.toString
                        } else {
                            value.get
                        }
                    } else {
                        c.getStringCellValue
                    }
            }
            .filter(_.nonEmpty)
            .toArray
    }

}
