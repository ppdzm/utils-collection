package io.github.ppdzm.utils.office.excel.chart

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.{Direction, LineChartGrouping}
import io.github.ppdzm.utils.office.excel.sheet.OOXMLSheet
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
class ColumnSeriesLineChart(val ooxmlSheet: OOXMLSheet,
                            override val chartTitle: String,
                            val direction: Direction.Value = Direction.vertical,
                            override val xAxisColumnName: String,
                            override val seriesNameColumns: Array[String],
                            val rowOffset: Int = 0,
                            val height: Int = 20,
                            val columnOffset: Int = 0,
                            val width: Int = 10,
                            override val grouping: LineChartGrouping.Value = LineChartGrouping.standard,
                            override val labeled: Boolean = false) extends LineChart with ColumnSeries {

    override def paddingData(ctLineChart: CTLineChart): Unit = {
        LineChart.paddingData(ctLineChart, labeled, xAxisValues, seriesMapping)
    }

}
