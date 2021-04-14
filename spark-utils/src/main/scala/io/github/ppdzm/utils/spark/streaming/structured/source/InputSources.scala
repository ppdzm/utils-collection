package io.github.ppdzm.utils.spark.streaming.structured.source

object InputSources {
    val fileSource: FileSource.type = FileSource
    val kafkaSource: KafkaSource.type = KafkaSource
    val rateSource: RateSource.type = RateSource
    val socketSource: SocketSource.type = SocketSource
}
