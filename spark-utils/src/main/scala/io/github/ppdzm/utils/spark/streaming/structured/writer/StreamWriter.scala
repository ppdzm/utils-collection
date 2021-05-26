package io.github.ppdzm.utils.spark.streaming.structured.writer

import io.github.ppdzm.utils.spark.streaming.structured.sink.{Sink, SinkOption}
import org.apache.spark.sql.streaming.{DataStreamWriter, OutputMode, Trigger}

import scala.reflect.ClassTag

class StreamWriter[T: ClassTag](private val dataStreamWriter: DataStreamWriter[T]) {
    def outputSink(sink: Sink): this.type = {
        dataStreamWriter.format(sink.toString)
        this
    }

    def trigger(trigger: Trigger): this.type = {
        dataStreamWriter.trigger(trigger)
        this
    }

    def outputMode(outputMode: OutputMode): this.type = {
        dataStreamWriter.outputMode(outputMode)
        this
    }

    def get: DataStreamWriter[T] = {
        this.dataStreamWriter
    }

    def checkpoint(path: String): this.type = {
        this.dataStreamWriter.option("checkpointLocation", path)
        this
    }

    def option(sinkOption: SinkOption, value: Boolean): this.type = {
        this.dataStreamWriter.option(sinkOption.toString, value)
        this
    }

    def option(sinkOption: SinkOption, value: Double): this.type = {
        this.dataStreamWriter.option(sinkOption.toString, value)
        this
    }

    def option(sinkOption: SinkOption, value: Long): this.type = {
        this.dataStreamWriter.option(sinkOption.toString, value)
        this
    }

    def option(sinkOption: SinkOption, value: String): this.type = {
        this.dataStreamWriter.option(sinkOption.toString, value)
        this
    }

}
