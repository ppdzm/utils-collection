package io.github.ppdzm.utils.office.excel.scala

import io.github.ppdzm.utils.office.excel.scala.chart.{ColumnSeriesBarChart, ValueSeriesBarChart}
import io.github.ppdzm.utils.office.excel.scala.enumeration.ExcelEnumerations.{BarChartGrouping, Direction, Order}
import io.github.ppdzm.utils.office.excel.scala.workbook.OOXMLWorkBook
import org.scalatest.FunSuite
import scalikejdbc.LoanPattern

class BarChartTest extends FunSuite {

    test("column series bar chart test") {
        val file = "test-bar-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包相关数据")
                new ColumnSeriesBarChart(
                    sheet, BarChartGrouping.standard.toString, Direction.vertical,
                    "日期", Array("红包领取前（PV）", "红包领取前（UV）", "红包领取成功（PV）", "红包领取成功（UV）"),
                    0, 20, 0, 10, BarChartGrouping.standard, labeled = false
                ).plot()
                new ColumnSeriesBarChart(
                    sheet, BarChartGrouping.stacked.toString, Direction.vertical,
                    "日期", Array("红包领取前（PV）", "红包领取前（UV）", "红包领取成功（PV）", "红包领取成功（UV）"),
                    0, 20, 10, 10, BarChartGrouping.stacked, labeled = false
                ).plot()
                new ColumnSeriesBarChart(
                    sheet, BarChartGrouping.percentStacked.toString, Direction.vertical,
                    "日期", Array("红包领取前（PV）", "红包领取前（UV）", "红包领取成功（PV）", "红包领取成功（UV）"),
                    20, 20, 0, 10, BarChartGrouping.percentStacked, labeled = false
                ).plot()
        }
    }

    test("value series bar chart test") {
        val file = "test-bar-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包领取个数分布")
                new ValueSeriesBarChart(
                    sheet, "standard", Direction.vertical,
                    "领取红包数", "用户数", "日期",
                    Array[String](), 0, 20, 0, 10,
                    Order.desc, 3, BarChartGrouping.standard, labeled = false
                ).plot()
                new ValueSeriesBarChart(
                    sheet, "stacked", Direction.horizontal,
                    "领取红包数", "老用户数", "日期",
                    Array[String](), 0, 20, 10, 10,
                    Order.desc, 3, BarChartGrouping.stacked, labeled = false
                ).plot()
                new ValueSeriesBarChart(
                    sheet, "percent stacked", Direction.horizontal,
                    "领取红包数", "老用户数", "日期",
                    Array[String](), 20, 20, 0, 10,
                    Order.desc, 3, BarChartGrouping.percentStacked, labeled = false
                ).plot()
        }
    }

}
