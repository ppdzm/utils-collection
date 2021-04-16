package io.github.ppdzm.utils.office.excel.sheet

import org.apache.poi.hssf.usermodel.HSSFWorkbook

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
case class OLE2Sheet(workbook: HSSFWorkbook, sheetName: String, overwrite: Boolean = false) extends PoiSheet
