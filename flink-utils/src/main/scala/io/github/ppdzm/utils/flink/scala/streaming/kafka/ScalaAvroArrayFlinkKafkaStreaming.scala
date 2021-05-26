package io.github.ppdzm.utils.flink.scala.streaming.kafka

import io.github.ppdzm.utils.flink.deserialization.ExtendedRegistryAvroDeserializationSchema
import org.apache.avro.generic.GenericData
import org.apache.flink.api.common.serialization.DeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait ScalaAvroArrayFlinkKafkaStreaming extends ScalaAvroFlinkKafkaStreaming[GenericData.Array[_]] {
    /**
     * [[DeserializationSchema]]
     */
    override protected lazy val deserializationSchema = new ExtendedRegistryAvroDeserializationSchema[GenericData.Array[_]](classOf[GenericData.Array[_]], schema, schemaCoderProvider)
}
