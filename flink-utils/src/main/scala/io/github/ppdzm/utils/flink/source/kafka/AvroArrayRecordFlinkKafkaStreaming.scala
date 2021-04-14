package io.github.ppdzm.utils.flink.source.kafka

import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.flink.formats.avro.RegistryAvroDeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait AvroArrayRecordFlinkKafkaStreaming extends AvroFlinkKafkaStreaming[GenericData.Array[GenericRecord]] {
    override protected lazy val deserializationSchema = new RegistryAvroDeserializationSchema(classOf[GenericData.Array[GenericRecord]], schema, schemaCoderProvider)
}
