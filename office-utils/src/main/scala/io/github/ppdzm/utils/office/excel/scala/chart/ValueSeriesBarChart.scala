package io.github.ppdzm.utils.office.excel.scala.chart

import io.github.ppdzm.utils.office.excel.scala.enumeration.ExcelEnumerations.{BarChartGrouping, Direction, Order}
import io.github.ppdzm.utils.office.excel.scala.sheet.OOXMLSheet
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
class ValueSeriesBarChart(val ooxmlSheet: OOXMLSheet,
                          override val chartTitle: String,
                          val direction: Direction.Value = Direction.vertical,
                          val xAxisColumnName: String,
                          val yAxisColumnName: String = "",
                          val seriesValueColumn: String,
                          val seriesValues: Array[String] = Array[String](),
                          val rowOffset: Int = 0,
                          val height: Int = 20,
                          val columnOffset: Int = 0,
                          val width: Int = 10,
                          val order: Order.Value = Order.desc,
                          val take: Int = 1,
                          override val grouping: BarChartGrouping.Value = BarChartGrouping.standard,
                          override val labeled: Boolean = false) extends BarChart with ValueSeries {


    override def paddingData(ctBarChart: CTBarChart): Unit = {
        BarChart.paddingData(ctBarChart, this.xAxisValues, this.seriesMapping)
    }

}
