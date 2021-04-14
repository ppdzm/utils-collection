package io.github.ppdzm.utils.spark

import io.github.ppdzm.utils.universal.base.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Stuart Alex on 2016/5/26.
 */
object SparkUtils extends Logging {

    def getSparkContext(conf: Map[String, String] = Map[String, String]()): SparkContext = {
        val sparkSession = this.getSparkSession(conf)
        this.logInfo("Get SparkContext ...")
        sparkSession.sparkContext
    }

    def getSparkSession(conf: Map[String, String] = Map[String, String]()): SparkSession = {
        this.logInfo("Get SparkSession ...")
        val builder = SparkSession.builder()
        builder.master("local[*]")
            .appName("unnamed")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.kryo.registrationRequired", "false")
            .config("spark.hadoop.hive.exec.dynamic.partition.mode", "nonstrict")
            .config("spark.blacklist.enabled", "false")
        sys.props.get("os.name").getOrElse("") match {
            case "Linux" => builder.enableHiveSupport()
            case "Ubuntu" => builder.enableHiveSupport()
            case "Mac OS X" =>
                if (System.getProperty("spark.hive.enabled", "false").toBoolean)
                    builder.enableHiveSupport()
            case _ =>
        }
        conf.foreach { case (key, value) => builder.config(key, value) }
        builder.getOrCreate()
    }

    def getStreamingContext(sparkConf: SparkConf, seconds: Int): StreamingContext = {
        new StreamingContext(sparkConf, Seconds(seconds))
    }

    def getMaster(sparkSession: SparkSession): String = sparkSession.sparkContext.master

    def getApplicationId(sparkSession: SparkSession): String = sparkSession.sparkContext.applicationId

}
