package io.github.ppdzm.utils.flink.deserialization;

import io.github.ppdzm.utils.flink.common.CachedSchemaCoderProvider;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
public class AvroDeserializationSchemaFactory {
    public static ExtendedRegistryAvroDeserializationSchema<GenericData.Array> forGenericArray(String topic, Schema reader, String schemaRegistryUrl, int identityMapCapacity) {
        return new ExtendedRegistryAvroDeserializationSchema<>(GenericData.Array.class, reader, new CachedSchemaCoderProvider(topic, schemaRegistryUrl, identityMapCapacity));
    }

    public static ExtendedRegistryAvroDeserializationSchema<GenericData.Array> forGenericArray(String topic, Schema reader, String schemaRegistryUrl) {
        return forGenericArray(topic, reader, schemaRegistryUrl, 1000);
    }

    public static ExtendedRegistryAvroDeserializationSchema<GenericRecord> forGenericRecord(String topic, Schema reader, String schemaRegistryUrl, int identityMapCapacity) {
        return new ExtendedRegistryAvroDeserializationSchema<>(GenericRecord.class, reader, new CachedSchemaCoderProvider(topic, schemaRegistryUrl, identityMapCapacity));
    }

    public static ExtendedRegistryAvroDeserializationSchema<GenericRecord> forGenericRecord(String topic, Schema reader, String schemaRegistryUrl) {
        return forGenericRecord(topic, reader, schemaRegistryUrl, 1000);
    }
}
