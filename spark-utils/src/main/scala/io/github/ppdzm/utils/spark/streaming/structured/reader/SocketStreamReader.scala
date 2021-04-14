package io.github.ppdzm.utils.spark.streaming.structured.reader

import org.apache.spark.sql.SparkSession
import org.sa.utils.spark.streaming.structured.source.InputSources.socketSource

case class SocketStreamReader(sparkSession: SparkSession, host: String, port: Int) extends StreamReader(sparkSession: SparkSession) {
    inputSource(socketSource)
        .option(socketSource.options.host, host)
        .option(socketSource.options.port, port)

    def includeTimestamp(value: Boolean = true): SocketStreamReader = {
        this.option(socketSource.options.includeTimestamp, value)
        this
    }

}
