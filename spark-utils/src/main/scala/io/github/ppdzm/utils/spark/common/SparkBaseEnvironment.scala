package io.github.ppdzm.utils.spark.common

import io.github.ppdzm.utils.spark.SparkUtils
import org.apache.spark.sql.SparkSession

trait SparkBaseEnvironment extends SparkConfigConstants {
    protected lazy val sparkSession: SparkSession = SparkUtils.getSparkSession(sparkSessionConf)
    protected val sparkSessionConf: Map[String, String]
}
