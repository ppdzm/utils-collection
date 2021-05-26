package io.github.ppdzm.utils.flink.scala.streaming.kafka

import io.github.ppdzm.utils.flink.deserialization.ExtendedRegistryAvroDeserializationSchema
import org.apache.avro.generic.GenericRecord
import org.apache.flink.api.common.serialization.DeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait ScalaAvroArrayRecordFlinkKafkaStreaming extends ScalaAvroFlinkKafkaStreaming[java.util.ArrayList[GenericRecord]] {
    /**
     * [[DeserializationSchema]]
     */
    override protected lazy val deserializationSchema = new ExtendedRegistryAvroDeserializationSchema[java.util.ArrayList[GenericRecord]](classOf[java.util.ArrayList[GenericRecord]], schema, schemaCoderProvider)
}
