package io.github.ppdzm.utils.flink.serialization;

import org.apache.avro.Schema;
import org.apache.flink.formats.avro.RegistryAvroSerializationSchema;
import org.apache.flink.formats.avro.SchemaCoder;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
public class ExtendedRegistryAvroSerializationSchema<T> extends RegistryAvroSerializationSchema<T> {
    private static final long serialVersionUID = -6419808648516287139L;

    public ExtendedRegistryAvroSerializationSchema(Class<T> recordClazz, Schema schema, SchemaCoder.SchemaCoderProvider schemaCoderProvider) {
        super(recordClazz, schema, schemaCoderProvider);
    }
}
