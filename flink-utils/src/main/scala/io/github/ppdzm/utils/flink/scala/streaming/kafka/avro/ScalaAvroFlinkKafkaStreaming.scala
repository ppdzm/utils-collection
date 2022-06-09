package io.github.ppdzm.utils.flink.scala.streaming.kafka.avro

import io.github.ppdzm.utils.flink.common.CachedSchemaCoderProvider
import io.github.ppdzm.utils.flink.scala.streaming.ScalaFlinkStreaming
import io.github.ppdzm.utils.flink.scala.streaming.kafka.FlinkKafkaValueStreaming
import org.apache.avro.Schema

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait ScalaAvroFlinkKafkaStreaming[T] extends FlinkKafkaValueStreaming[T] with ScalaFlinkStreaming[T] {
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
