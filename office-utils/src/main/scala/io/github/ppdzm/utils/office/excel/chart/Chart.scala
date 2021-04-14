package io.github.ppdzm.utils.office.excel.chart

import java.lang

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.Direction
import io.github.ppdzm.utils.office.excel.sheet.OOXMLSheet
import io.github.ppdzm.utils.universal.base.Logging
import org.apache.poi.xssf.usermodel.{XSSFChart, XSSFClientAnchor, XSSFDrawing, XSSFSheet}
import org.openxmlformats.schemas.drawingml.x2006.chart._

trait Chart extends Logging {
    protected lazy val ctChart: CTChart = xssfChart.getCTChart
    private lazy val chartStartRow: Int = rowOffset
    private lazy val chartEndRow: Int = chartStartRow + height
    private lazy val chartStartColumn: Int = sheet.getRow(0).getLastCellNum + columnOffset
    private lazy val chartEndColumn: Int = chartStartColumn + width
    private lazy val xssfDrawing: XSSFDrawing = sheet.createDrawingPatriarch()
    private lazy val xssfClientAnchor: XSSFClientAnchor = xssfDrawing.createAnchor(0, 0, 0, 0, this.chartStartColumn, this.chartStartRow, this.chartEndColumn, this.chartEndRow)
    private lazy val xssfChart: XSSFChart = xssfDrawing.createChart(xssfClientAnchor)
    val height: Int
    val width: Int
    val direction: Direction.Value
    val chartTypeName: String
    protected val ooxmlSheet: OOXMLSheet
    protected val sheet: XSSFSheet = ooxmlSheet.sheet
    protected val chartTitle: String
    protected val rowOffset: Int
    protected val columnOffset: Int

    def plot(): this.type

    protected def createNewChart(): AnyRef = {
        val ctPlotArea = ctChart.getPlotArea
        val newChart = this.invoke(ctPlotArea, "addNew" + this.chartTypeName)
        val ctBoolean = this.invoke(newChart, "addNewVaryColors")
        this.invoke(ctBoolean, "setVal", lang.Boolean.valueOf(true))
        newChart
    }

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
