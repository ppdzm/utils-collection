package io.github.ppdzm.utils.office.excel.chart

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.BarChartGrouping
import org.apache.poi.ss.usermodel.charts.ChartDataSource
import org.openxmlformats.schemas.drawingml.x2006.chart._

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
object BarChart {

    /**
     * 填充数据
     *
     * @param ctBarChart    CTBarChart
     * @param xAxisValues   x轴数据序列
     * @param seriesMapping 系列数据
     */
    def paddingData(ctBarChart: CTBarChart, xAxisValues: Array[String], seriesMapping: Map[String, ChartDataSource[Number]]): Unit = {
        val seriesNames = seriesMapping.keys.toArray
        for (index <- seriesNames.indices) {
            val name = seriesNames(index)
            val ctLineSeries = ctBarChart.addNewSer()
            ctLineSeries.addNewIdx().setVal(index)
            ctLineSeries.addNewTx().setV(name)
            ctLineSeries.addNewSpPr().addNewXfrm()
            val ctStringData = ctLineSeries.addNewCat().addNewStrLit()
            for (m <- xAxisValues.indices) {
                val stringValue = ctStringData.addNewPt()
                stringValue.setIdx(m)
                stringValue.setV(xAxisValues(m))
            }
            ctLineSeries.addNewVal().addNewNumRef().setF(seriesMapping(name).getFormulaString)
            ctLineSeries.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(Array[Byte](0, 0, 0))
        }
    }

}

trait BarChart extends Chart {
    val chartTypeName = "BarChart"
    protected val labeled: Boolean
    protected val grouping: BarChartGrouping.Value

    /**
     * 绘图
     *
     * @return
     */
    override def plot(): this.type = {
        this.logging.logInfo(s"start plot ${if (labeled) "labeled" else ""} ${this.getClass.getSimpleName} ${this.chartTitle} in sheet ${this.sheet.getSheetName}")
        this.setTitle(ctChart)
        this.setLegend(ctChart)
        this.setCategoryAxis(ctChart)
        this.setValueAxis(ctChart)
        //val ctBarChart = this.createNewBarChart()
        val ctBarChart = this.createNewChart().asInstanceOf[CTBarChart]
        this.setAxisIds(ctBarChart)
        this.paddingData(ctBarChart)
        this.setDataLabels(ctBarChart.addNewDLbls()).label(labeled)
        this
    }

    private def setAxisIds(ctBarChart: CTBarChart): Unit = {
        ctBarChart.addNewAxId().setVal(123456)
        ctBarChart.addNewAxId().setVal(123457)
    }

    /**
     * 填充数据
     *
     * @param ctBarChart CTBarChart
     */
    protected def paddingData(ctBarChart: CTBarChart): Unit

    private def createNewBarChart() = {
        val ctPlotArea = ctChart.getPlotArea
        val ctBarChart = ctPlotArea.addNewBarChart()
        ctBarChart.addNewVaryColors().setVal(true)
        ctBarChart.addNewBarDir().setVal(STBarDir.COL)
        ctBarChart.addNewGrouping().setVal(STBarGrouping.Enum.forString(grouping.toString))
        ctBarChart
    }

}
