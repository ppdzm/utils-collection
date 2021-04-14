package io.github.ppdzm.utils.flink.serde

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.specific.SpecificRecord
import org.apache.flink.formats.avro.{AvroDeserializationSchema, RegistryAvroDeserializationSchema}

object AvroDeserializationSchemaFactory {
    private val capacity = 1000

    def forGenericArray(schema: Schema, url: String, identityMapCapacity: Int = capacity): RegistryAvroDeserializationSchema[GenericData.Array[_]] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(null, url, identityMapCapacity)
        new RegistryAvroDeserializationSchema[GenericData.Array[_]](classOf[GenericData.Array[_]], schema, schemaCoderProvider)
    }

    def forGenericRecord(schema: Schema, url: String, identityMapCapacity: Int = capacity): RegistryAvroDeserializationSchema[GenericRecord] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(null, url, identityMapCapacity)
        new RegistryAvroDeserializationSchema[GenericRecord](classOf[GenericRecord], schema, schemaCoderProvider)
    }

    def forSpecific[T <: SpecificRecord](clazz: Class[T], schema: Schema, url: String, identityMapCapacity: Int = capacity): RegistryAvroDeserializationSchema[T] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(null, url, identityMapCapacity)
        new RegistryAvroDeserializationSchema[T](clazz, schema, schemaCoderProvider)
    }

    def forGenericRecord(schema: Schema): AvroDeserializationSchema[GenericRecord] = AvroDeserializationSchema.forGeneric(schema)

    def forSpecific[T <: SpecificRecord](clazz: Class[T], schema: Schema): AvroDeserializationSchema[T] = AvroDeserializationSchema.forSpecific(clazz)
}
