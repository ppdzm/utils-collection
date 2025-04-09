package io.github.ppdzm.utils.spark.streaming.kafka

import io.github.ppdzm.utils.spark.streaming.{PartitionProcessor, ResultProcessor}
import io.github.ppdzm.utils.universal.alert.{AlertConfig, Alerter, AlerterFactory}
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD

object KafkaSparkStreamingTest extends KafkaStreaming[String, String, String, Int] {
    override protected val config: Config = new FileConfig()
    override protected val applicationName: String = "test"
    override protected val alerter: Alerter = AlerterFactory.getAlerter("print", new AlertConfig(config))
    override protected val kafkaTopics = KAFKA_TOPICS.arrayValue()
    override protected val kafkaBrokers = KAFKA_BROKERS.stringValue
    override protected val kafkaConsumerGroupId = KAFKA_GROUP_ID.stringValue
    override protected val kafkaOffsetConfig = KAFKA_OFFSET_CONFIG.stringValue
    override protected val intervalInSeconds = SPARK_STREAMING_SECONDS.intValue
    override protected val sparkSessionConf: Map[String, String] = Map[String, String]()


    startWithProcessor(PartitionProcessorImpl, ResultProcessorImpl)
    // 或
    startWithProcessFunction(PartitionProcessorImpl.processPartition, N.handlePartitionProcessResult)

    /**
     * RDD处理逻辑
     *
     * @param rdd RDD[T]
     */
    override def processRDD(rdd: RDD[ConsumerRecord[String, String]]): RDD[String] = {
        rdd.map {
            _.value()
        }
    }

}

object N {
    /**
     * Partition处理逻辑
     *
     * @param partition Iterator[T]
     */
    def processPartition(partition: Iterator[String]): Int = {
        var count = 0
        partition.foreach {
            e =>
                println(e)
                count += 1
        }
        count
    }

    def handlePartitionProcessResult(result: Int): Unit = {
        println(s"read $result records in this partition")
    }
}

object PartitionProcessorImpl extends PartitionProcessor[String, Int] {
    /**
     * Partition处理逻辑
     *
     * @param partition Iterator[T]
     */
    override def processPartition(partition: Iterator[String]): Int = {
        var count = 0
        partition.foreach {
            e =>
                println(e)
                count += 1
        }
        count
    }
}

object ResultProcessorImpl extends ResultProcessor[Int] {
    private val logging = new Logging(getClass)

    /**
     * Partition处理逻辑
     *
     * @param result Iterator[T]
     */
    override def processResult(result: Int): Unit = {
        this.logging.logInfo(s"read $result records in this partition")
    }
}