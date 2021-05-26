package io.github.ppdzm.utils.spark.streaming.structured.writer

import io.github.ppdzm.utils.spark.streaming.structured.sink.Sinks.consoleSink
import org.apache.spark.sql.streaming.DataStreamWriter

import scala.reflect.ClassTag

case class ConsoleStreamWriter[T: ClassTag](dataStreamWriter: DataStreamWriter[T]) extends StreamWriter[T](dataStreamWriter) {
    this.outputSink(consoleSink)

    def truncate(boolean: Boolean): this.type = {
        this.option(consoleSink.options.truncate, boolean)
        this
    }

}