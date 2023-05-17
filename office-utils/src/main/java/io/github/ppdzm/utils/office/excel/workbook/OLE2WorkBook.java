package io.github.ppdzm.utils.office.excel.workbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;

/**
 * @author Created by Stuart Alex on 2023/5/16.
 */
public class OLE2WorkBook extends AbstractWorkBook {

    public OLE2WorkBook(String excelFileName) throws Exception {
        super(excelFileName);
        init(excelFileName, false, true);
    }

    public OLE2WorkBook(String excelFileName, boolean needBackup, boolean createWhenNotExist) throws Exception {
        super(excelFileName);
        init(excelFileName, needBackup, createWhenNotExist);
    }

    private void init(String excelFileName, boolean needBackup, boolean createWhenNotExist) throws Exception {
        checkExtension("xls");
        backup(needBackup, createWhenNotExist);
        if (excelFile.exists()) {
            workbook = new HSSFWorkbook(new FileInputStream(excelFileName));
        } else {
            workbook = new HSSFWorkbook();
        }
    }



}
