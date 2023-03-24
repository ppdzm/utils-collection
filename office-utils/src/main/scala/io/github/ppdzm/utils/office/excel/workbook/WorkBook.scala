package io.github.ppdzm.utils.office.excel.workbook

import io.github.ppdzm.utils.universal.base.Logging
import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.apache.poi.ss.usermodel.Workbook

import java.io.{File, FileNotFoundException, FileOutputStream}

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait WorkBook {
    protected lazy val logging = new Logging(getClass)
    protected lazy val backup: String = FilenameUtils.removeExtension(excelFileName) + "-backup." + FilenameUtils.getExtension(excelFileName)
    protected lazy val excelFile = new File(excelFileName)
    protected lazy val backupFile = new File(backup)
    protected val excelFileName: String
    protected val workbook: Workbook
    protected var success: Boolean = true

    /**
     * 备份原始文件
     *
     * @param create 没有时是否新建
     */
    def backup(needBackup: Boolean, create: Boolean): Unit = {
        if (!needBackup) {
            return
        }
        val backupFile = new File(backup)
        if (excelFile.exists()) {
            // 创建备份文件
            this.logging.logInfo(s"create backup file $backupFile")
            FileUtils.copyFile(excelFile, backupFile)
        } else if (!create) {
            throw new FileNotFoundException(s"file $excelFileName not exist")
        }
    }

    /**
     * 检查扩展名
     *
     * @param supportedExtensions 支持的扩展名
     */
    def checkExtension(supportedExtensions: String*): Unit = {
        val extension = FilenameUtils.getExtension(excelFileName)
        if (!supportedExtensions.contains(extension))
            throw new Exception(s"unsupported file extension, expected ${supportedExtensions.mkString(",")} but $extension")
    }

    /**
     * call this function by LoanPattern.using
     */
    def close(): Unit = {
        try {
            val fileOutputStream = new FileOutputStream(excelFileName)
            fileOutputStream.flush()
            this.workbook.write(fileOutputStream)
            fileOutputStream.close()
            this.workbook.close()
        } catch {
            case e: Exception =>
                this.success = false
                throw e
        } finally {
            this.cleanBackup(this.success)
        }
    }

    /**
     * 清理备份文件
     *
     * @param success
     */
    def cleanBackup(success: Boolean): Unit = {
        if (!success && excelFile.exists()) {
            // 删除出错的文件
            this.logging.logInfo(s"delete wrong file ${excelFile.getName}")
            excelFile.delete()
            if (backupFile.exists()) {
                // 恢复原文件
                this.logging.logInfo(s"recover backup ${backupFile.getName} to ${excelFile.getName}")
                FileUtils.copyFile(backupFile, excelFile)
            }
        }
        if (backupFile.exists()) {
            // 删除备份文件
            this.logging.logInfo(s"delete backup file $backupFile")
            backupFile.delete()
        }
    }

}
