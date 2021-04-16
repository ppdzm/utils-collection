package io.github.ppdzm.utils.office.excel.chart

import io.github.ppdzm.utils.office.excel.sheet.OOXMLSheet
import org.apache.poi.ss.usermodel.charts.ChartDataSource

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait Series {
    protected lazy val xAxisValues: Array[String] = ooxmlSheet.getColumnData(xAxisColumnName).distinct
    protected lazy val seriesMapping: Map[String, ChartDataSource[Number]] = getSeries
    protected val ooxmlSheet: OOXMLSheet
    protected val xAxisColumnName: String

    def getSeries: Map[String, ChartDataSource[Number]]

}
