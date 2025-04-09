package io.github.ppdzm.utils.office.excel.scala

import io.github.ppdzm.utils.office.excel.scala.workbook.{JxlWorkBook, OLE2WorkBook, OOXMLWorkBook}
import org.scalatest.FunSuite
import scalikejdbc.LoanPattern

class SimpleExcelTest extends FunSuite {

    test("write excel by jxl") {
        val file = "test-jxl.xls"
        val rows = List(
            List(1, 1.2345, 0.12),
            List("2", "2.3456", "0.1234")
        )
        JxlWorkBook(file)
            .writeSheet("a", overwrite = false, List("A", "B"), rows)
            .writeSheet("b", overwrite = false, List("X", "Y", "环比"), rows)
            .writeSheet("a", overwrite = false, List("A", "B"), rows)
            .writeSheet("b", overwrite = true, List("X", "Y", "环比"), rows)
            .close()
    }

    test("write excel by poi hssf") {
        val file = "test-poi-hssf.xls"
        val rows = List(
            List(1, 1.2345, 0.12),
            List("2", "2.3456", "0.1234")
        )
        LoanPattern.using(OLE2WorkBook(file)) {
            workbook =>
                workbook.writeSheet("a", overwrite = false, List("A", "B"), rows)
                    .writeSheet("b", overwrite = false, List("X", "Y", "环比"), rows)
                    .writeSheet("a", overwrite = false, List("A", "B"), rows)
                    .writeSheet("b", overwrite = true, List("X", "Y", "环比"), rows)
        }
    }

    test("write excel by poi xssf") {
        val file = "test-poi-xssf.xlsx"
        val rows = List(
            List(1, 1.2345, 0.12),
            List("2", "2.3456", "0.1234")
        )
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                ooxmlWorkBook
                    .writeSheet("a", overwrite = false, List("A", "B"), rows)
                    .writeSheet("b", overwrite = false, List("X", "Y", "环比"), rows)
                    .writeSheet("a", overwrite = false, List("A", "B"), rows)
                    .writeSheet("b", overwrite = true, List("X", "Y", "环比"), rows)
        }
    }

}
