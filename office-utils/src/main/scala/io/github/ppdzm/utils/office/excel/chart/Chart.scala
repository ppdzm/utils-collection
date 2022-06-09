package io.github.ppdzm.utils.office.excel.chart

import java.lang

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.Direction
import io.github.ppdzm.utils.office.excel.sheet.OOXMLSheet
import io.github.ppdzm.utils.universal.base.LoggingTrait
import org.apache.poi.xssf.usermodel.{XSSFChart, XSSFClientAnchor, XSSFDrawing, XSSFSheet}
import org.openxmlformats.schemas.drawingml.x2006.chart._

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait Chart extends LoggingTrait {
    /**
     * 图表
     */
    protected lazy val ctChart: CTChart = xssfChart.getCTChart
    /**
     * 图表开始的行
     */
    private lazy val chartStartRow: Int = rowOffset
    /**
     * 图表结束的行
     */
    private lazy val chartEndRow: Int = chartStartRow + height
    /**
     * 图表开始的列
     */
    private lazy val chartStartColumn: Int = sheet.getRow(0).getLastCellNum + columnOffset
    /**
     * 图表结束的列
     */
    private lazy val chartEndColumn: Int = chartStartColumn + width
    /**
     * 画笔
     */
    private lazy val xssfDrawing: XSSFDrawing = sheet.createDrawingPatriarch()
    /**
     * 对齐
     */
    private lazy val xssfClientAnchor: XSSFClientAnchor = xssfDrawing.createAnchor(0, 0, 0, 0, this.chartStartColumn, this.chartStartRow, this.chartEndColumn, this.chartEndRow)
    /**
     * 画布
     */
    private lazy val xssfChart: XSSFChart = xssfDrawing.createChart(xssfClientAnchor)
    /**
     * 图表高度
     */
    val height: Int
    /**
     * 图表高度
     */
    val width: Int
    /**
     * 图表方向
     */
    val direction: Direction.Value
    /**
     * 图表名称
     */
    val chartTypeName: String
    /**
     * 图表所在boxed sheet
     */
    protected val ooxmlSheet: OOXMLSheet
    /**
     * 图表所在poi sheet
     */
    protected val sheet: XSSFSheet = ooxmlSheet.sheet
    /**
     * 图表标题
     */
    protected val chartTitle: String
    /**
     * 图表行位移
     */
    protected val rowOffset: Int
    /**
     * 图表列位移
     */
    protected val columnOffset: Int

    /**
     * 绘图
     *
     * @return
     */
    def plot(): this.type

    /**
     * 创建新的图表
     *
     * @return
     */
    protected def createNewChart(): AnyRef = {
        val ctPlotArea = ctChart.getPlotArea
        val newChart = this.invoke(ctPlotArea, "addNew" + this.chartTypeName)
        val ctBoolean = this.invoke(newChart, "addNewVaryColors")
        this.invoke(ctBoolean, "setVal", lang.Boolean.valueOf(true))
        newChart
    }

    /**
     * 隐式调用
     *
     * @param obj        图表
     * @param method     方法名称
     * @param parameters 参数
     * @return
     */
    private def invoke(obj: AnyRef, method: String, parameters: AnyRef = null): AnyRef = {
        if (parameters != null)
            obj.getClass.getMethods.find(_.getName == method).get.invoke(obj, parameters)
        else
            obj.getClass.getMethods.find(_.getName == method).get.invoke(obj)
    }

    protected def setLegend(ctChart: CTChart): Unit = {
        val ctLegend = ctChart.addNewLegend()
        // 图例项说明防止于底部
        ctLegend.addNewLegendPos().setVal(STLegendPos.B)
        // 禁止覆盖图例项
        ctLegend.addNewOverlay().setVal(false)
    }

    protected def setTitle(ctChart: CTChart): Unit = {
        val ctTitle = CTTitle.Factory.newInstance
        // 禁止覆盖标题
        ctTitle.addNewOverlay().setVal(false)
        val ctText = ctTitle.addNewTx()
        val ctStringData = CTStrData.Factory.newInstance
        val ctStringVal = ctStringData.addNewPt()
        ctStringVal.setIdx(123456)
        ctStringVal.setV(this.chartTitle)
        ctText.addNewStrRef().setStrCache(ctStringData)
        ctChart.setTitle(ctTitle)
    }

    protected def setCategoryAxis(ctChart: CTChart): Unit = {
        val ctPlotArea: CTPlotArea = ctChart.getPlotArea
        val ctCatAx = ctPlotArea.addNewCatAx()
        ctCatAx.addNewCrossesAt().setVal(0)
        ctCatAx.addNewAxId().setVal(123456)
        val ctScaling = ctCatAx.addNewScaling()
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX)
        ctCatAx.addNewDelete().setVal(false)
        ctCatAx.addNewAxPos().setVal(STAxPos.B)
        ctCatAx.addNewCrossAx().setVal(123456)
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO)
    }

    protected def setValueAxis(ctChart: CTChart): Unit = {
        val ctPlotArea: CTPlotArea = ctChart.getPlotArea
        val ctValAx = ctPlotArea.addNewValAx()
        ctValAx.addNewCrossesAt().setVal(0)
        ctValAx.addNewAxId().setVal(123457)
        val ctScaling = ctValAx.addNewScaling()
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX)
        ctValAx.addNewDelete().setVal(false)
        ctValAx.addNewAxPos().setVal(STAxPos.L)
        ctValAx.addNewCrossAx().setVal(123457)
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO)
    }

    protected def setDataLabels(dataLabels: CTDLbls): DataLabelSetter = {
        DataLabelSetter(dataLabels)
    }

    case class DataLabelSetter(dataLabels: CTDLbls) {
        dataLabels.addNewShowSerName().setVal(false)
        dataLabels.addNewShowCatName().setVal(false)
        dataLabels.addNewShowLegendKey().setVal(false)

        def label(visible: Boolean): this.type = {
            dataLabels.addNewDelete().setVal(!visible)
            this
        }

        def position(position: STDLblPos.Enum): this.type = {
            dataLabels.addNewDLblPos().setVal(position)
            this
        }

    }

}
