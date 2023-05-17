package io.github.ppdzm.utils.office.excel.scala.workbook

import io.github.ppdzm.utils.office.excel.scala.sheet.OLE2Sheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

import java.io.FileInputStream

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
case class OLE2WorkBook(excelFileName: String, needBackUp: Boolean = false, private val createWhenNotExist: Boolean = true) extends WorkBook {
    this.checkExtension("xls")
    this.backup(needBackUp, createWhenNotExist)
    override val workbook: HSSFWorkbook = {
        if (excelFile.exists()) {
            new HSSFWorkbook(new FileInputStream(excelFileName))
        } else {
            new HSSFWorkbook()
        }
    }

    /**
     * 获取sheet
     *
     * @param sheetName sheet名称
     * @param overwrite 复写
     * @return
     */
    def getSheet(sheetName: String, overwrite: Boolean = false): OLE2Sheet = {
        OLE2Sheet(this.workbook, sheetName, overwrite)
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
