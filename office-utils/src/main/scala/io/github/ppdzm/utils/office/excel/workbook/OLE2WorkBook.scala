package io.github.ppdzm.utils.office.excel.workbook

import java.io.FileInputStream

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.sa.utils.office.excel.sheet.OLE2Sheet

case class OLE2WorkBook(excelFileName: String, private val createWhenNotExist: Boolean = true) extends WorkBook {
    this.checkExtension("xls")
    this.backup(createWhenNotExist)
    override val workbook: HSSFWorkbook = {
        if (excelFile.exists()) {
            new HSSFWorkbook(new FileInputStream(excelFileName))
        } else {
            new HSSFWorkbook()
        }
    }

    def getSheet(sheetName: String, overwrite: Boolean = false): OLE2Sheet = {
        OLE2Sheet(this.workbook, sheetName, overwrite)
    }

    def writeSheet(sheetName: String, overwrite: Boolean, columns: List[String] = List[String](), rows: List[List[Any]]): this.type = {
        try {
            OLE2Sheet(this.workbook, sheetName, overwrite)
                .writeColumnHeader(columns)
                .writeData(rows)
        } catch {
            case e: Exception =>
                this.close()
                throw e
        }
        this
    }

}
