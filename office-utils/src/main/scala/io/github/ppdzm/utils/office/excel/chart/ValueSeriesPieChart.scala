package io.github.ppdzm.utils.office.excel.chart

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.{Direction, Order}
import io.github.ppdzm.utils.office.excel.sheet.OOXMLSheet
import org.openxmlformats.schemas.drawingml.x2006.chart.{CTPie3DChart, CTPieChart}

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
class ValueSeriesPieChart(val ooxmlSheet: OOXMLSheet,
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
                          override val labeled: Boolean = false,
                          override val is3D: Boolean = false) extends PieChart with ValueSeries {

    override protected def paddingData(ctPieChart: CTPieChart): Unit = {
        val ctPieSeries = ctPieChart.addNewSer()
        PieChart.paddingData(ctPieSeries, this.xAxisValues, this.seriesMapping)
    }

    override protected def paddingData(ctPie3DChart: CTPie3DChart): Unit = {
        val ctPieSeries = ctPie3DChart.addNewSer()
        PieChart.paddingData(ctPieSeries, this.xAxisValues, this.seriesMapping)
    }

}
