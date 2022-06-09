package io.github.ppdzm.utils.flink.scala.deserialization

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

import java.nio.charset.StandardCharsets


/**
 * @author Created by Stuart Alex on 2022/4/6.
 */
class KafkaConsumerRecordDeserializationSchema extends KafkaDeserializationSchema[ConsumerRecord[String, String]] {
    override def isEndOfStream(t: ConsumerRecord[String, String]): Boolean = {
        false
    }

    override def deserialize(consumerRecord: ConsumerRecord[Array[Byte], Array[Byte]]): ConsumerRecord[String, String] = {
        val key =
            if (consumerRecord.key() != null) {
                new String(consumerRecord.key(), StandardCharsets.UTF_8)
            } else {
                null
            }
        val value =
            if (consumerRecord.value() != null) {
                new String(consumerRecord.value(), StandardCharsets.UTF_8);
            } else {
                null
            }
        new ConsumerRecord[String, String](
            consumerRecord.topic(),
            consumerRecord.partition(),
            consumerRecord.offset(),
            key,
            value
        )
    }

    override def getProducedType: TypeInformation[ConsumerRecord[String, String]] = {
        TypeInformation.of(classOf[ConsumerRecord[String, String]])
    }
}
