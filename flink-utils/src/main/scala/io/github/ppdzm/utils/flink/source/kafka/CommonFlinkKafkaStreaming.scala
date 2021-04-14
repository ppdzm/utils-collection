package io.github.ppdzm.utils.flink.source.kafka

import java.util.Properties

import io.github.ppdzm.utils.flink.source.JavaFlinkStreaming
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.sa.utils.flink.source.JavaFlinkStreaming
import org.sa.utils.hadoop.kafka.config.KafkaConsumerProperties
import io.github.ppdzm.utils.universal.implicits.ArrayConversions._
import io.github.ppdzm.utils.universal.implicits.ExtendedJavaConversions._

/**
 * Created by Stuart Alex on 2021/4/3.
 */
trait CommonFlinkKafkaStreaming[T] extends JavaFlinkStreaming[T] {
    protected val kafkaSourceTopic: String
    protected val kafkaBrokers: String
    protected val autoCommit: Boolean
    protected val consumerGroupId: String
    protected val additionalConsumerConfig: Map[String, AnyRef]
    protected val deserializationSchema: DeserializationSchema[T]
    protected lazy val consumerProperties: Properties =
        KafkaConsumerProperties.builder()
            .BOOTSTRAP_SERVERS(kafkaBrokers)
            .AUTO_OFFSET_RESET(offsetResetStrategy)
            .ENABLE_AUTO_COMMIT(autoCommit)
            .GROUP_ID(consumerGroupId)
            .invoke(additionalConsumerConfig)
            .build()
    override protected lazy val dataSource: SourceFunction[T] = {
        logInfo("consumer configuration is following:\n" + consumerProperties.toKeyValuePair.withKeySorted.withKeyPadded(-1, "\t", "\t", "").mkString("\n"))
        new FlinkKafkaConsumer(kafkaSourceTopic, deserializationSchema, consumerProperties)
            .setStartFromGroupOffsets()
            .setCommitOffsetsOnCheckpoints(true)
    }
}
