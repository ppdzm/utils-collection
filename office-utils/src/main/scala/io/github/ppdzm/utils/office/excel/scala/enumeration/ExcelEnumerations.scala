package io.github.ppdzm.utils.office.excel.scala.enumeration

import io.github.ppdzm.utils.universal.base.Enum

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
object ExcelEnumerations {

    object BarChartGrouping extends Enum {
        val percentStacked: BarChartGrouping.Value = Value("percentStacked")
        val stacked: BarChartGrouping.Value = Value("stacked")
        val standard: BarChartGrouping.Value = Value("standard")
    }

    object ChartType extends Enum {
        val bar: ChartType.Value = Value("bar")
        val line: ChartType.Value = Value("line")
        val pie: ChartType.Value = Value("pie")
    }

    object Direction extends Enum {
        val horizontal: Direction.Value = Value("horizontal")
        val vertical: Direction.Value = Value("vertical")
    }

    object LineChartGrouping extends Enum {
        val percentStacked: LineChartGrouping.Value = Value("percentStacked")
        val stacked: LineChartGrouping.Value = Value("stacked")
        val standard: LineChartGrouping.Value = Value("standard")
    }

    object Order extends Enum {
        val asc: Order.Value = Value("asc")
        val desc: Order.Value = Value("desc")
    }

    object SeriesType extends Enum {
        val column: SeriesType.Value = Value("column")
        val value: SeriesType.Value = Value("value")
    }

}
