package io.github.ppdzm.utils.flink.source.kafka

import io.github.ppdzm.utils.flink.serde.CachedSchemaCoderProvider
import org.apache.avro.Schema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait AvroFlinkKafkaStreaming[T] extends JavaFlinkKafkaStreaming[T] {
    /**
     * [[org.apache.flink.formats.avro.SchemaCoder.SchemaCoderProvider]]
     */
    protected lazy val schemaCoderProvider = new CachedSchemaCoderProvider(null, schemaRegistryUrl, identityMapCapacity)
    /**
     * [[Schema]]
     */
    protected val schema: Schema
    /**
     * Schema Registry地址
     */
    protected val schemaRegistryUrl: String
    /**
     * Schema Registry容量
     */
    protected val identityMapCapacity: Int = 1000
}
