package io.github.ppdzm.utils.spark.streaming.structured.sink

object Sinks {
    val kafkaSink: KafkaSink.type = KafkaSink
    val consoleSink: ConsoleSink.type = ConsoleSink
}
