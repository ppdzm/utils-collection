package io.github.ppdzm.utils.spark.streaming.redis

import io.github.ppdzm.utils.spark.streaming.kafka.{PP, RP}
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.sa.utils.spark.streaming.kafka.{PP, RP}
import org.sa.utils.universal.config.Config

object RedisSparkStreamingTest extends RedisStreaming[String, Int] with App {
    override protected val config: Config = FileConfig()
    override protected val sparkConf: SparkConf = new SparkConf()
    override protected val sparkSessionConf: Map[String, String] = Map[String, String]()

    startWithProcessor(PP, RP)

    /**
     * RDD处理逻辑
     *
     * @param rdd RDD[T]
     */
    override def processRDD(rdd: RDD[(String, String)]): RDD[String] = {
        rdd.map(_._2)
    }

}
