package io.github.ppdzm.utils.office.excel.chart

import org.apache.poi.ss.usermodel.charts.ChartDataSource
import org.sa.utils.office.excel.sheet.OOXMLSheet

trait Series {
    protected lazy val xAxisValues: Array[String] = ooxmlSheet.getColumnData(xAxisColumnName).distinct
    protected lazy val seriesMapping: Map[String, ChartDataSource[Number]] = getSeries
    protected val ooxmlSheet: OOXMLSheet
    protected val xAxisColumnName: String

    def getSeries: Map[String, ChartDataSource[Number]]

}
