package io.github.ppdzm.utils.office.excel.scala.chart

import io.github.ppdzm.utils.office.excel.scala.enumeration.ExcelEnumerations.LineChartGrouping.Value
import org.apache.poi.ss.usermodel.charts._
import org.openxmlformats.schemas.drawingml.x2006.chart._

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
object LineChart {
    /**
     * 填充数据
     *
     * @param ctLineChart   CTLineChart
     * @param xAxisValues   x轴数据序列
     * @param seriesMapping 系列数据
     */
    def paddingData(ctLineChart: CTLineChart, labeled: Boolean, xAxisValues: Array[String], seriesMapping: Map[String, ChartDataSource[Number]]): Unit = {
        val seriesNames = seriesMapping.keys.toArray
        for (index <- seriesNames.indices) {
            val name = seriesNames(index)
            // 添加新的系列
            val ctLineSeries = ctLineChart.addNewSer()
            // 是否是平滑曲线，默认为true
            // ctLineSeries.addNewSmooth().setVal(false)
            if (!labeled) {
                // 设置标记形状，默认为自动选择
                ctLineSeries.addNewMarker().addNewSymbol().setVal(STMarkerStyle.NONE)
            }
            // 设置系列index
            ctLineSeries.addNewIdx().setVal(index)
            // 设置系列名称
            ctLineSeries.addNewTx().setV(name)
            // 设置系列数据
            val ctStringData = ctLineSeries.addNewCat().addNewStrLit()
            for (m <- xAxisValues.indices) {
                val stringValue = ctStringData.addNewPt()
                stringValue.setIdx(m)
                stringValue.setV(xAxisValues(m))
            }
            ctLineSeries.addNewVal().addNewNumRef().setF(seriesMapping(name).getFormulaString)
            //ctLineSeries.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(Array[Byte](0, 0, 0))
        }
    }
}

trait LineChart extends Chart {
    val chartTypeName = "LineChart"
    protected val grouping: Value
    protected val labeled: Boolean

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
        //val ctLineChart = this.createNewLineChart()
        val ctLineChart = this.createNewChart().asInstanceOf[CTLineChart]
        this.setAxisIds(ctLineChart)
        this.setDataLabels(ctLineChart.addNewDLbls()).label(labeled).position(STDLblPos.T)
        this.paddingData(ctLineChart)
        this
    }

    private def setAxisIds(ctLineChart: CTLineChart): Unit = {
        ctLineChart.addNewAxId().setVal(123456)
        ctLineChart.addNewAxId().setVal(123457)
    }

    /**
     * 填充数据
     *
     * @param ctLineChart CTLineChart
     */
    protected def paddingData(ctLineChart: CTLineChart): Unit

    private def createNewLineChart(): CTLineChart = {
        val ctPlotArea = ctChart.getPlotArea
        val ctLineChart = ctPlotArea.addNewLineChart()
        ctLineChart.addNewVaryColors().setVal(true)
        ctLineChart.addNewGrouping().setVal(STGrouping.Enum.forString(grouping.toString))
        ctLineChart
    }

}
