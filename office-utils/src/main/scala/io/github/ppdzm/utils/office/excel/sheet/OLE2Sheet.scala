package io.github.ppdzm.utils.office.excel.sheet

import org.apache.poi.hssf.usermodel.HSSFWorkbook

case class OLE2Sheet(workbook: HSSFWorkbook, sheetName: String, overwrite: Boolean = false) extends PoiSheet
