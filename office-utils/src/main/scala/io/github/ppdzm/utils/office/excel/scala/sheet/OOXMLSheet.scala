package io.github.ppdzm.utils.office.excel.scala.sheet

import java.text.DecimalFormat

import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import scala.util.Try

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
case class OOXMLSheet(workbook: XSSFWorkbook, sheetName: String, overwrite: Boolean = false) extends PoiSheet {
    override val sheet: XSSFSheet = getSheet(overwrite).asInstanceOf[XSSFSheet]
    protected val firstDataRowIndex = 1
    protected val lastDataRowIndex: Int = sheet.getLastRowNum

    /**
     * 获取一列数据
     *
     * @param columnName 列标题
     * @return
     */
    def getColumnData(columnName: String): Array[String] = {
        getColumnData(locateColumn(columnName))
    }

    /**
     * 定位列标题列号
     *
     * @param columnName 列标题
     * @return
     */
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

    /**
     * 获取一列数据
     *
     * @param columnIndex 列号
     * @return
     */
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
