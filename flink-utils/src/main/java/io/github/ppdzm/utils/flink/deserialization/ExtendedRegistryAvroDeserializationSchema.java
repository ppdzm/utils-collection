package io.github.ppdzm.utils.flink.deserialization;

import org.apache.avro.Schema;
import org.apache.flink.formats.avro.RegistryAvroDeserializationSchema;
import org.apache.flink.formats.avro.SchemaCoder;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class ExtendedRegistryAvroDeserializationSchema<T> extends RegistryAvroDeserializationSchema<T> {
    private Schema schema;
    private Class<T> recordClazz;

    public ExtendedRegistryAvroDeserializationSchema(Class<T> recordClazz,
                                                     Schema reader,
                                                     SchemaCoder.SchemaCoderProvider schemaCoderProvider) {
        super(recordClazz, reader, schemaCoderProvider);
        this.schema = reader;
        this.recordClazz = recordClazz;
    }

}
