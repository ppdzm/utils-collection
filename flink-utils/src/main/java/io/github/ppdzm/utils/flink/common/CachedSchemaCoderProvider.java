package io.github.ppdzm.utils.flink.common;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.flink.formats.avro.SchemaCoder;
import org.apache.flink.formats.avro.registry.confluent.ConfluentSchemaRegistryCoder;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class CachedSchemaCoderProvider implements SchemaCoder.SchemaCoderProvider {
    private static final long serialVersionUID = 4396760227837662376L;
    private String subject;
    private String schemaRegistryUrl;
    private int identityMapCapacity;

    public CachedSchemaCoderProvider(String subject, String schemaRegistryUrl, int identityMapCapacity) {
        super();
        this.subject = subject;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.identityMapCapacity = identityMapCapacity;
    }

    @Override
    public SchemaCoder get() {
        if (subject == null || subject.isEmpty()) {
            return new ConfluentSchemaRegistryCoder(subject, new CachedSchemaRegistryClient(schemaRegistryUrl, identityMapCapacity));
        } else {
            return new ConfluentSchemaRegistryCoder(new CachedSchemaRegistryClient(schemaRegistryUrl, identityMapCapacity));
        }
    }
}
