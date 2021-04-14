package io.github.ppdzm.utils.spark.common

import org.apache.spark.sql.SparkSession
import org.sa.utils.spark.SparkUtils

trait SparkBaseEnvironment extends SparkConfigConstants {
    protected val sparkSessionConf: Map[String, String]
    protected lazy val sparkSession: SparkSession = SparkUtils.getSparkSession(sparkSessionConf)
}
