package io.github.ppdzm.utils.flink.scala.streaming.kafka.avro

import io.github.ppdzm.utils.flink.deserialization.ExtendedRegistryAvroDeserializationSchema
import org.apache.avro.generic.GenericRecord
import org.apache.flink.api.common.serialization.DeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait ScalaAvroRecordFlinkKafkaStreaming extends ScalaAvroFlinkKafkaStreaming[GenericRecord] {
    /**
     * [[DeserializationSchema]]
     */
    override protected lazy val deserializationSchema = new ExtendedRegistryAvroDeserializationSchema[GenericRecord](classOf[GenericRecord], schema, schemaCoderProvider)
}
