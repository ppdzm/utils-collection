package io.github.ppdzm.utils.hadoop.kafka

import java.util.Properties

import io.github.ppdzm.utils.hadoop.kafka.config.KafkaConsumerProperties
import io.github.ppdzm.utils.hadoop.kafka.pool.KafkaConsumerPool
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2017/4/1.
 */
object KafkaUtils {

    /**
     * 获取Topic列表
     *
     * @return
     */
    def getTopicList(kafkaBrokers: String): List[String] = {
        val properties: Properties =
            KafkaConsumerProperties
                .builder()
                .BOOTSTRAP_SERVERS(kafkaBrokers)
                .KEY_DESERIALIZER_CLASS[StringDeserializer]
                .VALUE_DESERIALIZER_CLASS[StringDeserializer]
                .build()
        Pool.borrow(KafkaConsumerPool(properties)) { consumer => consumer.listTopics().map(_._1) }
            .toList
            .sorted
    }

    /**
     * 获取指定Topic的分区数
     *
     * @param topic 指定的Topic
     * @return
     */
    def getPartitionNumber(kafkaBrokers: String, topic: String): Int = {
        val properties: Properties =
            KafkaConsumerProperties
                .builder()
                .BOOTSTRAP_SERVERS(kafkaBrokers)
                .KEY_DESERIALIZER_CLASS[StringDeserializer]
                .VALUE_DESERIALIZER_CLASS[StringDeserializer]
                .build()
        Pool.borrow(KafkaConsumerPool(properties)) { consumer => consumer.partitionsFor(topic).size() }
    }

}