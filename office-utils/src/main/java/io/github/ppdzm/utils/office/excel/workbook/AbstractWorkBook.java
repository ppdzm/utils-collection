package io.github.ppdzm.utils.office.excel.workbook;

import io.github.ppdzm.utils.universal.base.Logging;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
public abstract class AbstractWorkBook implements WorkBook {
    Logging logging = new Logging(getClass());
    Workbook workbook;
    String excelFileName;
    String backup;
    File excelFile;
    File backupFile;

    public AbstractWorkBook(String excelFileName) {
        this.excelFileName = excelFileName;
        this.backup = FilenameUtils.removeExtension(excelFileName) + "-backup." + FilenameUtils.getExtension(excelFileName);
        this.excelFile = new File(excelFileName);
        this.backupFile = new File(backup);
    }

    /**
     * 备份原始文件
     *
     * @param needBackup 是否需要备份
     * @param create     如果原始文件不存在，是否创建
     * @throws IOException IOException
     */
    public void backup(boolean needBackup, boolean create) throws IOException {
        if (excelFile.exists()) {
            if (needBackup) {
                // 创建备份文件
                this.logging.logInfo("create backup file " + backupFile);
                FileUtils.copyFile(excelFile, backupFile);
            }
        } else if (!create) {
            throw new FileNotFoundException("file " + excelFileName + " not exist");
        }
    }

    @Override
    public void checkExtension(String... supportedExtensions) throws Exception {
        String extension = FilenameUtils.getExtension(excelFileName);
        for (String supportedExtension : supportedExtensions) {
            if (extension.equals(supportedExtension)) {
                return;
            }
        }
        throw new Exception("unsupported file extension, expected " + String.join(",", supportedExtensions) + " but got " + extension);
    }

    /**
     * 清理备份文件
     *
     * @param success 是否成功，成功则只需删除备份文件，失败则需恢复原始文件后再删除备份文件
     * @throws IOException IOException
     */
    public void cleanBackup(boolean success) throws IOException {
        if (!success && excelFile.exists()) {
            // 删除出错的文件
            this.logging.logInfo("delete wrong file " + excelFile.getName());
            boolean deleteResult = excelFile.delete();
            if (deleteResult && backupFile.exists()) {
                // 恢复原文件
                this.logging.logInfo("recover backup " + backupFile.getName() + " to " + excelFile.getName());
                FileUtils.copyFile(backupFile, excelFile);
            }
        }
        if (backupFile.exists()) {
            // 删除备份文件
            this.logging.logInfo("delete backup file " + backupFile);
            boolean deleteResult = backupFile.delete();
            this.logging.logInfo("backup file " + backupFile + " deleted");
        }
    }

    @Override
    public void close() throws IOException {
        boolean success = true;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(excelFileName);
            fileOutputStream.flush();
            this.workbook.write(fileOutputStream);
            fileOutputStream.close();
            this.workbook.close();
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            this.cleanBackup(success);
        }
    }

}
