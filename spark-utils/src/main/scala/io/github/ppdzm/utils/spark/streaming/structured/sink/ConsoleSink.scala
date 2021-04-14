package io.github.ppdzm.utils.spark.streaming.structured.sink

private[sink] object ConsoleSink extends Sink {

    object options extends SinkOptions {

        object truncate extends SinkOption

    }

}
