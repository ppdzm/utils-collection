package io.github.ppdzm.utils.office.excel.workbook

import io.github.ppdzm.utils.office.excel.sheet.JxlSheet
import jxl.{Workbook, WorkbookSettings}
import org.apache.poi.ss.usermodel

case class JxlWorkBook(excelFileName: String,
                       private val createWhenNotExist: Boolean = true) extends WorkBook {
    override val workbook: usermodel.Workbook = null
    this.checkExtension("xls")
    this.backup(createWhenNotExist)
    private val jxlWorkbook = {
        val workbookSettings = new WorkbookSettings()
        workbookSettings.setEncoding("ISO-8859-1")
        if (excelFile.exists()) {
            val book = Workbook.getWorkbook(excelFile)
            Workbook.createWorkbook(excelFile, book, workbookSettings)
        } else {
            Workbook.createWorkbook(excelFile, workbookSettings)
        }
    }

    def writeSheet(sheetName: String, overwrite: Boolean, columns: List[String] = List[String](), rows: List[List[Any]]): this.type = {
        try {
            JxlSheet(this.jxlWorkbook, sheetName, overwrite)
                .writeColumnHeader(columns)
                .writeData(rows)
        } catch {
            case e: Exception =>
                this.success = false
                this.close()
                throw e
        }
        this
    }

    override def close(): Unit = {
        this.jxlWorkbook.write()
        this.jxlWorkbook.close()
        this.cleanBackup(success)
    }
}
