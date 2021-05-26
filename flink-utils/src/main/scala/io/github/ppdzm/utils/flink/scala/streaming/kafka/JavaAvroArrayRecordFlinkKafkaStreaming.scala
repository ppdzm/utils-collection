package io.github.ppdzm.utils.flink.scala.streaming.kafka

import io.github.ppdzm.utils.flink.deserialization.ExtendedRegistryAvroDeserializationSchema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.flink.api.common.serialization.DeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait JavaAvroArrayRecordFlinkKafkaStreaming extends JavaAvroFlinkKafkaStreaming[GenericData.Array[GenericRecord]] {
    /**
     * [[DeserializationSchema]]
     */
    override protected lazy val deserializationSchema = new ExtendedRegistryAvroDeserializationSchema[GenericData.Array[GenericRecord]](classOf[GenericData.Array[GenericRecord]], schema, schemaCoderProvider)
}
