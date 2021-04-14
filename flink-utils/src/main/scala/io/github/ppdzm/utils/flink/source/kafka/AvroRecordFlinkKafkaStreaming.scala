package io.github.ppdzm.utils.flink.source.kafka

import org.apache.avro.generic.GenericRecord
import org.apache.flink.formats.avro.RegistryAvroDeserializationSchema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait AvroRecordFlinkKafkaStreaming extends AvroFlinkKafkaStreaming[GenericRecord] {
    override protected lazy val deserializationSchema = new RegistryAvroDeserializationSchema(classOf[GenericRecord], schema, schemaCoderProvider)
}
