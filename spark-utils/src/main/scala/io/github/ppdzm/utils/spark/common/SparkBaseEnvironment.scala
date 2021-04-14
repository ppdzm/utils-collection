package io.github.ppdzm.utils.spark.common

import org.apache.spark.sql.SparkSession
import io.github.ppdzm.utils.spark.SparkUtils

trait SparkBaseEnvironment extends SparkConfigConstants {
    protected lazy val sparkSession: SparkSession = SparkUtils.getSparkSession(sparkSessionConf)
    protected val sparkSessionConf: Map[String, String]
}
