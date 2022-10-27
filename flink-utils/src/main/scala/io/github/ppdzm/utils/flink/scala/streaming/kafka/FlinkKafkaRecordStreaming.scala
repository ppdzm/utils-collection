package io.github.ppdzm.utils.flink.scala.streaming.kafka

import io.github.ppdzm.utils.universal.implicits.ArrayConversions._
import io.github.ppdzm.utils.universal.implicits.ExtendedJavaConversions._
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, KafkaDeserializationSchema}

/**
 * @author Created by Stuart Alex on 2021/4/30.
 */
trait FlinkKafkaRecordStreaming[T] extends FlinkKafkaStreaming[T] {
    /**
     * [[KafkaDeserializationSchema]]
     */
    protected val deserializationSchema: KafkaDeserializationSchema[T]
    /**
     * 数据源方法
     */
    override protected lazy val dataSource: SourceFunction[T] = {
        this.logging.logInfo("consumer configuration is following:\n" + consumerProperties.toKeyValuePair.withKeySorted.withKeyPadded(-1, "\t", "\t", "").mkString("\n"))
        new FlinkKafkaConsumer(kafkaSourceTopic, deserializationSchema, consumerProperties)
            .setStartFromGroupOffsets()
            .setCommitOffsetsOnCheckpoints(true)
    }
}
