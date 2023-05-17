package io.github.ppdzm.utils.office.excel.scala.workbook

import io.github.ppdzm.utils.office.excel.scala.sheet.OOXMLSheet
import org.apache.poi.openxml4j.util.ZipSecureFile
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.FileInputStream

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
case class OOXMLWorkBook(excelFileName: String, needBackup: Boolean = true, private val createWhenNotExist: Boolean = true) extends WorkBook {
    this.checkExtension("xlsx")
    this.backup(needBackup, createWhenNotExist)
    ZipSecureFile.setMinInflateRatio(-1.0d)
    override val workbook: XSSFWorkbook = {
        if (excelFile.exists()) {
            new XSSFWorkbook(new FileInputStream(excelFileName))
        } else {
            new XSSFWorkbook()
        }
    }

    /**
     * 获取sheet
     *
     * @param sheetName sheet名称
     * @param overwrite 复写
     * @return
     */
    def getSheet(sheetName: String, overwrite: Boolean = false): OOXMLSheet = {
        OOXMLSheet(this.workbook, sheetName, overwrite)
    }

    /**
     * 写sheet
     *
     * @param sheetName sheet名称
     * @param overwrite 复写
     * @param columns   列标题
     * @param rows      行数据
     * @return
     */
    def writeSheet(sheetName: String, overwrite: Boolean, columns: List[String] = List[String](), rows: List[List[Any]]): this.type = {
        try {
            OOXMLSheet(this.workbook, sheetName, overwrite)
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

}
