package io.github.ppdzm.utils.spark.udaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataTypes, StructType}

/**
 * Created by Stuart Alex on 2017/5/1.
 */
class CollectAsList extends UserDefinedAggregateFunction {
    private val separator = ","
    private val initialValue = ""

    override def inputSchema = new StructType().add("input", DataTypes.StringType)

    override def bufferSchema = new StructType().add("buffer", DataTypes.StringType)

    override def dataType = DataTypes.StringType

    override def deterministic = true

    override def initialize(buffer: MutableAggregationBuffer) = {
        buffer.update(0, this.initialValue)
    }

    override def update(buffer: MutableAggregationBuffer, input: Row) = {
        buffer.update(0, if (buffer.getString(0) == this.initialValue) input.getString(0) else buffer.getString(0) + this.separator + input.getString(0))
    }

    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {
        val v1s = buffer1.getString(0).split(this.separator)
        val v2 = buffer2.getString(0)
        if (!v1s.contains(v2)) {
            if (buffer1.getString(0) == this.initialValue)
                buffer1.update(0, v2)
            else
                buffer1.update(0, buffer1.getString(0) + this.separator + v2)
        }
    }

    override def evaluate(buffer: Row) = {
        buffer.getString(0)
    }

}