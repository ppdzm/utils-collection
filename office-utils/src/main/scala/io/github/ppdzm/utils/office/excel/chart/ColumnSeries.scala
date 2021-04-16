package io.github.ppdzm.utils.office.excel.chart

import org.apache.poi.ss.usermodel.charts.{ChartDataSource, DataSources}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait ColumnSeries extends Series {
    //  protected lazy val xAxis: ChartDataSource[String] = {
    //    val sheet: XSSFSheet = ooxmlSheet.sheet
    //    val xAxisColumnIndex = ooxmlSheet.locateColumn(xAxisColumnName)
    //    val firstRow = 1
    //    val lastRow = sheet.getLastRowNum
    //    DataSources.fromStringCellRange(sheet, new CellRangeAddress(firstRow, lastRow, xAxisColumnIndex, xAxisColumnIndex))
    //  }
    protected val seriesNameColumns: Array[String]

    override def getSeries: Map[String, ChartDataSource[Number]] = {
        val sheet: XSSFSheet = ooxmlSheet.sheet
        val series = seriesNameColumns.map {
            seriesName =>
                val seriesColumnIndex = ooxmlSheet.locateColumn(seriesName)
                val firstRow = 1
                val lastRow = sheet.getLastRowNum
                seriesName -> DataSources.fromNumericCellRange(sheet, new CellRangeAddress(firstRow, lastRow, seriesColumnIndex, seriesColumnIndex))
        }
        series.toMap
    }

}
