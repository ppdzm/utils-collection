package io.github.ppdzm.utils.office.excel.scala.chart

import io.github.ppdzm.utils.office.excel.scala.enumeration.ExcelEnumerations.{Direction, LineChartGrouping, Order}
import io.github.ppdzm.utils.office.excel.scala.sheet.OOXMLSheet
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
class ValueSeriesLineChart(val ooxmlSheet: OOXMLSheet,
                           override val chartTitle: String,
                           val direction: Direction.Value = Direction.vertical,
                           override val xAxisColumnName: String,
                           override val yAxisColumnName: String = "",
                           override val seriesValueColumn: String,
                           override val seriesValues: Array[String] = Array[String](),
                           val rowOffset: Int = 0,
                           val height: Int = 20,
                           val columnOffset: Int = 0,
                           val width: Int = 10,
                           val order: Order.Value = Order.desc,
                           override val take: Int = 1,
                           override val grouping: LineChartGrouping.Value = LineChartGrouping.standard,
                           override val labeled: Boolean = false) extends LineChart with ValueSeries {

    override def paddingData(ctLineChart: CTLineChart): Unit = {
        LineChart.paddingData(ctLineChart, labeled, this.xAxisValues, this.seriesMapping)
    }

}
