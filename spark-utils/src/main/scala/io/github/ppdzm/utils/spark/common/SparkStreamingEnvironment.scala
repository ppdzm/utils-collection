package io.github.ppdzm.utils.spark.common

import io.github.ppdzm.utils.universal.config.ConfigItem
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.sa.utils.spark.SparkUtils

trait SparkStreamingEnvironment extends SparkBaseEnvironment {
    protected lazy val SPARK_STREAMING_SECONDS: ConfigItem = ConfigItem("spark.streaming.seconds", 5)
    protected val intervalInSeconds: Int
    protected val sparkConf: SparkConf
    protected lazy val streamingContext: StreamingContext = SparkUtils.getStreamingContext(sparkConf, intervalInSeconds)
}
