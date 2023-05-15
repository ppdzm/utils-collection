package io.github.ppdzm.utils.flink.scala.streaming.kafka

import io.github.ppdzm.utils.flink.scala.streaming.FlinkStreaming
import io.github.ppdzm.utils.hadoop.scala.kafka.config.KafkaConsumerProperties
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, KafkaDeserializationSchema}
import org.apache.kafka.clients.consumer.OffsetResetStrategy

import java.util.Properties

/**
 * @author Created by Stuart Alex on 2021/4/30.
 */
trait FlinkKafkaStreaming[T] extends FlinkStreaming[T] {
    /**
     * Kafka源Topic
     */
    protected val kafkaSourceTopic: String
    /**
     * Kafka源Brokers
     */
    protected val kafkaBrokers: String
    /**
     * Kafka消费者是否自动提交
     */
    protected val autoCommit: Boolean
    /**
     * Kafka消费者GROUP ID
     */
    protected val consumerGroupId: String
    /**
     * Kafka消费者附加配置
     */
    protected val additionalConsumerConfig: Map[String, AnyRef]
    /**
     * Kafka消费者OffsetResetStrategy
     */
    protected val offsetResetStrategy: OffsetResetStrategy
    /**
     * Kafka消费者配置
     */
    protected lazy val consumerProperties: Properties =
        KafkaConsumerProperties.builder()
            .BOOTSTRAP_SERVERS(kafkaBrokers)
            .AUTO_OFFSET_RESET(offsetResetStrategy)
            .ENABLE_AUTO_COMMIT(autoCommit)
            .GROUP_ID(consumerGroupId)
            .invoke(additionalConsumerConfig)
            .build()
}
