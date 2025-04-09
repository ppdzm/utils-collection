package io.github.ppdzm.utils.spark.streaming.redis

import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object RedisSparkStreamingTest extends RedisStreaming[String, Int] with App {
    override protected val config: Config = new FileConfig()
    override protected val sparkConf: SparkConf = new SparkConf()
    override protected val sparkSessionConf: Map[String, String] = Map[String, String]()


    /**
     * RDD处理逻辑
     *
     * @param rdd RDD[T]
     */
    override def processRDD(rdd: RDD[(String, String)]): RDD[String] = {
        rdd.map(_._2)
    }

}
