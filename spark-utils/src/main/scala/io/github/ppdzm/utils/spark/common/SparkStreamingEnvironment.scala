package io.github.ppdzm.utils.spark.common

import io.github.ppdzm.utils.spark.SparkUtils
import io.github.ppdzm.utils.universal.config.ConfigItem
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext

trait SparkStreamingEnvironment extends SparkBaseEnvironment {
    protected lazy val SPARK_STREAMING_SECONDS: ConfigItem = new ConfigItem(config, "spark.streaming.seconds", 5)
    protected lazy val streamingContext: StreamingContext = SparkUtils.getStreamingContext(sparkConf, intervalInSeconds)
    protected val intervalInSeconds: Int
    protected val sparkConf: SparkConf
}
