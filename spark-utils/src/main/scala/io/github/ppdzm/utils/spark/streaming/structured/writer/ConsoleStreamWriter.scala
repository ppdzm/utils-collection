package io.github.ppdzm.utils.spark.streaming.structured.writer

import org.apache.spark.sql.streaming.DataStreamWriter
import org.sa.utils.spark.streaming.structured.sink.Sinks.consoleSink

import scala.reflect.ClassTag

case class ConsoleStreamWriter[T: ClassTag](dataStreamWriter: DataStreamWriter[T]) extends StreamWriter[T](dataStreamWriter) {
    this.outputSink(consoleSink)

    def truncate(boolean: Boolean): this.type = {
        this.option(consoleSink.options.truncate, boolean)
        this
    }

}