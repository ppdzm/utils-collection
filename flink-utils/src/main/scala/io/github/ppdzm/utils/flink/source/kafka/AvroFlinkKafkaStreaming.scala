package io.github.ppdzm.utils.flink.source.kafka

import io.github.ppdzm.utils.flink.serde.CachedSchemaCoderProvider
import org.apache.avro.Schema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait AvroFlinkKafkaStreaming[T] extends CommonFlinkKafkaStreaming[T] {
    protected val schema: Schema
    protected val schemaRegistryUrl: String
    protected val identityMapCapacity: Int = 1000
    protected lazy val schemaCoderProvider = new CachedSchemaCoderProvider(null, schemaRegistryUrl, identityMapCapacity)
}
