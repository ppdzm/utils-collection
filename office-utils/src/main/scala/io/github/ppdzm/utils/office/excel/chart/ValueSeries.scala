package io.github.ppdzm.utils.office.excel.chart

import io.github.ppdzm.utils.office.excel.enumeration.ExcelEnumerations.Order
import org.apache.poi.ss.usermodel.charts.{ChartDataSource, DataSources}
import org.apache.poi.ss.util.CellRangeAddress

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
trait ValueSeries extends Series {
    val yAxisColumnName: String
    val seriesValueColumn: String
    val seriesValues: Array[String]
    val order: Order.Value
    val take: Int

    override def getSeries: Map[String, ChartDataSource[Number]] = {
        val seriesData = ooxmlSheet.getColumnData(seriesValueColumn)
        val seriesNames = if (seriesValues.nonEmpty)
            seriesValues
        else if (order == Order.desc && take != 0)
            seriesData.distinct.sorted.takeRight(take)
        else if (take != 0)
            seriesData.distinct.sorted.take(take)
        else
            seriesData.distinct.sorted
        val series = seriesNames.map {
            seriesName =>
                val yAxisColumnIndex = ooxmlSheet.locateColumn(yAxisColumnName)
                val firstRow = seriesData.zipWithIndex.find(_._1 == seriesName).map(_._2).getOrElse(seriesData.length - 1)
                val lastRow = seriesData.zipWithIndex.reverse.find(_._1 == seriesName).map(_._2).getOrElse(seriesData.length - 1)
                seriesName -> DataSources.fromNumericCellRange(ooxmlSheet.sheet, new CellRangeAddress(firstRow, lastRow, yAxisColumnIndex, yAxisColumnIndex))
        }
        series.toMap
    }

}
