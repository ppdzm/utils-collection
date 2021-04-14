package io.github.ppdzm.utils.office.excel.chart

import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart
import org.sa.utils.office.excel.enumeration.ExcelEnumerations.{Direction, LineChartGrouping}
import org.sa.utils.office.excel.sheet.OOXMLSheet

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
