package io.github.ppdzm.utils.flink.deserialization;

import org.apache.avro.Schema;
import org.apache.flink.formats.avro.RegistryAvroDeserializationSchema;
import org.apache.flink.formats.avro.SchemaCoder;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class ExtendedRegistryAvroDeserializationSchema<T> extends RegistryAvroDeserializationSchema<T> {
    private static final long serialVersionUID = -2148793352732459370L;
    private final Schema schema;
    private final Class<T> recordClazz;

    public ExtendedRegistryAvroDeserializationSchema(Class<T> recordClazz,
                                                     Schema reader,
                                                     SchemaCoder.SchemaCoderProvider schemaCoderProvider) {
        super(recordClazz, reader, schemaCoderProvider);
        this.schema = reader;
        this.recordClazz = recordClazz;
    }

}
