package io.github.ppdzm.utils.flink.serde

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.specific.SpecificRecord
import org.apache.flink.formats.avro.{AvroSerializationSchema, RegistryAvroSerializationSchema}

object AvroSerializationSchemaFactory {
    private val capacity = 1000

    def forGenericArray(schema: Schema, subject: String, url: String, identityMapCapacity: Int = capacity): RegistryAvroSerializationSchema[GenericData.Array[_]] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(subject, url, identityMapCapacity)
        new RegistryAvroSerializationSchema[GenericData.Array[_]](classOf[GenericData.Array[_]], schema, schemaCoderProvider)
    }

    def forGenericRecord(schema: Schema, subject: String, url: String, identityMapCapacity: Int = capacity): RegistryAvroSerializationSchema[GenericRecord] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(subject, url, identityMapCapacity)
        new RegistryAvroSerializationSchema[GenericRecord](classOf[GenericRecord], schema, schemaCoderProvider)
    }

    def forSpecific[T <: SpecificRecord](clazz: Class[T], schema: Schema, subject: String, url: String, identityMapCapacity: Int = capacity): RegistryAvroSerializationSchema[T] = {
        val schemaCoderProvider = new CachedSchemaCoderProvider(subject, url, identityMapCapacity)
        new RegistryAvroSerializationSchema[T](clazz, schema, schemaCoderProvider)
    }

    def forGenericRecord(schema: Schema): AvroSerializationSchema[GenericRecord] = AvroSerializationSchema.forGeneric(schema)

    def forSpecific[T <: SpecificRecord](clazz: Class[T]): AvroSerializationSchema[T] = AvroSerializationSchema.forSpecific(clazz)
}
