package io.github.ppdzm.utils.office.excel

import io.github.ppdzm.utils.office.excel.chart.{ColumnSeriesLineChart, ValueSeriesLineChart}
import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.{Direction, LineChartGrouping, Order}
import io.github.ppdzm.utils.office.excel.workbook.OOXMLWorkBook
import org.scalatest.FunSuite
import scalikejdbc.LoanPattern

class LineChartTest extends FunSuite {

    test("column series line chart test") {
        val file = "test-line-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包相关数据")
                new ColumnSeriesLineChart(
                    sheet, "红包领取用户数变化趋势", Direction.vertical,
                    "日期", Array("红包领取前（PV）", "红包领取前（UV）", "红包领取成功（PV）", "红包领取成功（UV）"),
                    0, 20, 0, 10
                ).plot()
                new ColumnSeriesLineChart(
                    sheet, "红包领取用户数变化趋势", Direction.vertical,
                    "日期", Array("红包领取前（PV）", "红包领取前（UV）", "红包领取成功（PV）", "红包领取成功（UV）"),
                    20, 20, 0, 10, labeled = true
                ).plot()
        }
    }

    test("value series line chart test") {
        val file = "test-line-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包领取个数分布")
                new ValueSeriesLineChart(
                    sheet, "近三天红包分布对比", Direction.vertical,
                    "领取红包数", "用户数", "日期",
                    rowOffset = 0, height = 20, columnOffset = 0, width = 10,
                    order = Order.desc, take = 3, grouping = LineChartGrouping.standard
                ).plot()
                new ValueSeriesLineChart(
                    sheet, "近三天红包分布对比", Direction.vertical,
                    "领取红包数", "用户数", "日期",
                    rowOffset = 20, height = 20, columnOffset = 0, width = 10,
                    order = Order.desc, take = 3, labeled = true
                ).plot()
        }
    }

}
