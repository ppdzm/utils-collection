package io.github.ppdzm.utils.office.excel

import io.github.ppdzm.utils.office.excel.workbook.{OLE2WorkBook, OOXMLWorkBook}
import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite

import java.io.File
import java.nio.charset.StandardCharsets
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class TemporaryTest extends FunSuite {

    test("read headers") {
        val path = "data/office/excel"
        val files = FileUtils
          .listFiles(new File(path), Array("xls"), true)
          .toList
        val sets = ListBuffer[String]()
        files.foreach {
            file =>
                if (!file.getName.startsWith(".~")) {
                    println(file.getAbsolutePath)
                    val workbook = OLE2WorkBook(file.getAbsolutePath, createWhenNotExist = false)
                    workbook.workbook.sheetIterator().foreach {
                        s =>
                            if (s.getLastRowNum > 1) {
                                val sheet = workbook.getSheet(s.getSheetName)
                                val row0 = sheet.getRowValues(0)
                                val headers = if (row0 == null || row0.length == 1 || row0.head.toString.contains("www"))
                                    sheet.getRowValues(1)
                                else
                                    row0
                                if (headers != null)
                                    headers
                                      .foreach {
                                          h =>
                                              if (h != null) {
                                                  println(h)
                                                  sets += h.toString
                                              }
                                      }
                            }
                    }
                }
        }
        println("============")
        sets.distinct.foreach(println)
    }

    test("read journal") {
        val path = "data.nosync/office/excel"
        val files = FileUtils
          .listFiles(new File(path), Array("xls"), true)
          .toList
        val columns = List(
            "company_name",
            "telephone",
            "contactor",
            "tax",
            "email",
            "exh_position",
            "mobile",
            "qq",
            "is_special",
            "issue",
            "category",
            "industry_category",
            "primary_catalog",
            "position",
            "url"
        )
        val mapping = Map(
            "公司名" -> "company_name",
            "企业名称" -> "company_name",
            "公司名称" -> "company_name",
            "展商名称" -> "company_name",
            "电话" -> "telephone",
            "办公电话" -> "telephone",
            "手机、电话" -> "telephone",
            "电话/手机" -> "telephone",
            "联系人" -> "contactor",
            "业务联系人" -> "contactor",
            "传真" -> "tax",
            "邮箱" -> "email",
            "展位号" -> "exh_position",
            "本届展位" -> "exh_position",
            "129届展位号" -> "exh_position",
            "展位" -> "exh_position",
            "手机" -> "mobile",
            "手机号" -> "mobile",
            "QQ" -> "qq",
            "特装展商" -> "is_special",
            "期数" -> "issue",
            "类目" -> "category",
            "行业类目" -> "industry_category",
            "一级目录" -> "primary_catalog",
            "职位" -> "position",
            "职务" -> "position",
            "网址" -> "url",
            "企业网站" -> "url",
            "官网" -> "url",
            "网站" -> "url"
        )
        var number = 1
        files.foreach {
            file =>
                if (!file.getName.startsWith(".~")) {
                    println(file.getAbsolutePath)
                    LoanPattern.using(OLE2WorkBook(file.getAbsolutePath, createWhenNotExist = false)) {
                        workbook =>
                            workbook.workbook.sheetIterator().foreach {
                                s =>
                                    if (s.getLastRowNum > 1) {
                                        println(s.getSheetName)
                                        val sheet = workbook.getSheet(s.getSheetName)
                                        val row0 = sheet.getRowValues(0)
                                        val (headerRowIndex, headers) = if (row0 == null || row0.length == 1 || row0.head.toString.contains("www"))
                                            (1, sheet.getRowValues(1))
                                        else
                                            (0, row0)
                                        if (headers != null) {
                                            println(headers.mkString("\t"))
                                            val sheetData = ListBuffer[Array[String]]()
                                            (headerRowIndex + 1 until s.getLastRowNum + 1).foreach {
                                                rowIndex =>
                                                    val row = s.getRow(rowIndex)
                                                    if (row != null) {
                                                        val rowData = new Array[String](columns.length + 2)
                                                        rowData(0) = number.toString
                                                        number += 1
                                                        rowData(1) = file.getName.replace(".xls", "")
                                                        (0 until row.getLastCellNum).foreach {
                                                            excelColIndex =>
                                                                //                                                            println(rowIndex + " " + colIndex + " " + headers.length)
                                                                if (excelColIndex < headers.length) {
                                                                    val colName = headers.get(excelColIndex)
                                                                    if (mapping.contains(colName)) {
                                                                        val hiveColIndex = columns.indexOf(mapping(colName))
                                                                        val cell = row.getCell(excelColIndex)
                                                                        val cellValue = {
                                                                            val cv = sheet.getCellValue(cell)
                                                                            if (cv == null || cv == "null")
                                                                                ""
                                                                            else
                                                                                cv.replace("\r\n", "").replace("\r", "").replace("\n", "")
                                                                        }
                                                                        rowData(hiveColIndex + 2) = cellValue
                                                                    }
                                                                }
                                                        }
                                                        rowData.indices.foreach {
                                                            i =>
                                                                if (rowData(i) == null)
                                                                    rowData(i) = ""
                                                        }
                                                        sheetData += rowData
                                                        FileUtils.write(new File("journal.txt"), rowData.mkString('\001'.toString) + "\n", StandardCharsets.UTF_8, true)
                                                    }
                                            }
                                        }
                                    }
                            }
                    }
                    //                    sys.exit(0)
                }
        }
    }

    test("read exhibition") {
        val fileName = "data.nosync/office/2019-2022会刊采购数据表@0302的副本.xlsx"
        val file = new File(fileName)
        LoanPattern.using(OOXMLWorkBook(file.getAbsolutePath, createWhenNotExist = false)) {
            workbook =>
                val sheet = workbook.getSheet("Sheet1")
                val cols = Array(0, 1, 2, 4, 5, 6)

                val s = sheet.sheet
                (1 to s.getLastRowNum).foreach {
                    rowIndex =>
                        val row = s.getRow(rowIndex)
                        val rowData = new Array[String](6)
                        (0 until 6).foreach {
                            colIndex =>
                                val cell = row.getCell(cols(colIndex))
                                rowData(colIndex) = sheet.getCellValue(cell)
                        }
                        FileUtils.write(new File("exhibition.txt"), rowData.mkString('\001'.toString) + "\n", StandardCharsets.UTF_8, true)
                }
        }
    }

}
