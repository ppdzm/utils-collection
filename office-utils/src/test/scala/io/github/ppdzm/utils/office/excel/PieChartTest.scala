package io.github.ppdzm.utils.office.excel

import io.github.ppdzm.utils.office.excel.chart.{ColumnSeriesPieChart, ValueSeriesPieChart}
import io.github.ppdzm.utils.office.excel.workbook.OOXMLWorkBook
import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.scalatest.FunSuite

class PieChartTest extends FunSuite {

    test("value series pie chart") {
        val file = "test-pie-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包领取个数分布")
                new ValueSeriesPieChart(
                    sheet, "近4天红包分布对比", xAxisColumnName = "领取红包数", yAxisColumnName = "用户数", seriesValueColumn = "日期",
                    width = 8, take = 4, labeled = true, is3D = true
                ).plot()
                new ValueSeriesPieChart(
                    sheet, "近3天红包分布对比", xAxisColumnName = "领取红包数", yAxisColumnName = "用户数", seriesValueColumn = "日期",
                    columnOffset = 8, width = 8, take = 3, is3D = true
                ).plot()
                new ValueSeriesPieChart(
                    sheet, "近2天红包分布对比", xAxisColumnName = "领取红包数", yAxisColumnName = "用户数", seriesValueColumn = "日期",
                    rowOffset = 20, width = 8, take = 2, labeled = true
                ).plot()
                new ValueSeriesPieChart(
                    sheet, "近1天红包分布对比", xAxisColumnName = "领取红包数", yAxisColumnName = "用户数", seriesValueColumn = "日期",
                    columnOffset = 8, rowOffset = 20, width = 8, take = 1
                ).plot()
        }
    }

    test("column series pie chart") {
        val file = "test-pie-chart.xlsx"
        LoanPattern.using(OOXMLWorkBook(file)) {
            ooxmlWorkBook =>
                val sheet = ooxmlWorkBook.getSheet("红包相关数据")
                new ColumnSeriesPieChart(
                    sheet, "aaa", xAxisColumnName = "日期",
                    width = 8, labeled = true, is3D = true, seriesNameColumns = Array("红包领取前（PV）")
                ).plot()
                new ColumnSeriesPieChart(
                    sheet, "bbb", xAxisColumnName = "日期",
                    columnOffset = 8, width = 8, is3D = true, seriesNameColumns = Array("红包领取前（UV）")
                ).plot()
                new ColumnSeriesPieChart(
                    sheet, "ccc", xAxisColumnName = "日期",
                    rowOffset = 20, width = 8, labeled = true, seriesNameColumns = Array("红包领取成功（PV）")
                ).plot()
                new ColumnSeriesPieChart(
                    sheet, "ddd", xAxisColumnName = "日期",
                    columnOffset = 8, rowOffset = 20, width = 8, seriesNameColumns = Array("红包领取成功（UV）")
                ).plot()
        }
    }

}
