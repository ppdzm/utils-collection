package io.github.ppdzm.utils.spark.streaming.structured.writer

import io.github.ppdzm.utils.spark.streaming.structured.sink.Sinks.kafkaSink
import org.apache.spark.sql.streaming.DataStreamWriter

import scala.reflect.ClassTag

class KafkaStreamWriter[T: ClassTag](dataStreamWriter: DataStreamWriter[T], brokers: String) extends StreamWriter[T](dataStreamWriter) {
    this.outputSink(kafkaSink).option(kafkaSink.options.`kafka.bootstrap.servers`, brokers)

    def topic(topic: String): this.type = {
        this.option(kafkaSink.options.topic, topic)
    }

}
