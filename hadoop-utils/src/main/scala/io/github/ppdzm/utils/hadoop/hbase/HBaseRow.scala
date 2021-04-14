package io.github.ppdzm.utils.hadoop.hbase

import io.github.ppdzm.utils.universal.base.functions
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.JavaConversions._
import scala.collection.mutable


/**
 * Created by Stuart Alex on 2017/12/1.
 */
object HBaseRow {

    def apply(result: Result): HBaseRow = {
        val rowKey = Bytes.toString(result.getRow).trim
        val data = mutable.Map[String, Map[String, (Long, String)]]()
        val cells = result.listCells()
        cells.foreach(cell => {
            val family = Bytes.toString(CellUtil.cloneFamily(cell)).trim
            val qualifier = Bytes.toString(CellUtil.cloneQualifier(cell)).trim
            if (qualifier != "body") {
                val valueBytes = CellUtil.cloneValue(cell)
                val value = if (valueBytes.length == 8) {
                    val string = Bytes.toString(valueBytes)
                    if (string.isUnderstandable)
                        string.trim
                    else
                        functions.bytes2Long(valueBytes).toString
                } else {
                    Bytes.toString(valueBytes).trim
                }
                val timestamp = cell.getTimestamp
                if (data.containsKey(family)) {
                    if (data(family).get(qualifier).isEmpty)
                        data(family) += qualifier -> (timestamp, value)
                } else {
                    data += family -> Map(qualifier -> (timestamp, value))
                }
            }
        })
        HBaseRow(rowKey, data.toMap)
    }

}

case class HBaseRow(rowKey: String, data: Map[String, Map[String, (Long, String)]]) {

    override def toString: String = JsonUtils.serialize(this)


}