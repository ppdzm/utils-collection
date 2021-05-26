package io.github.ppdzm.utils.flink.streaming.kafka;

import io.github.ppdzm.utils.flink.common.CachedSchemaCoderProvider;
import io.github.ppdzm.utils.flink.deserialization.ExtendedRegistryAvroDeserializationSchema;
import io.github.ppdzm.utils.flink.streaming.kafka.config.KafkaStreaming;
import io.github.ppdzm.utils.universal.alert.Alerter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.avro.Schema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AvroKafkaStreaming<T> extends KafkaStreaming<T> {
    /**
     * [[Schema]]
     */
    private Schema schema;
    /**
     * Schema Registry地址
     */
    private String schemaRegistryUrl;
    /**
     * Schema Registry容量
     */
    private int identityMapCapacity;
    private CachedSchemaCoderProvider schemaCoderProvider;
    private Class<T> recordClazz;

//    public AvroKafkaStreaming(String applicationName,
//                              Alerter alerter,
//                              CheckpointConfigItems checkpointConfiguration,
//                              String kafkaBrokers,
//                              String kafkaSourceTopic,
//                              OffsetResetStrategy offsetResetStrategy,
//                              boolean autoCommit,
//                              String consumerGroupId,
//                              Map<String, String> additionalConsumerConfig,
//                              Schema schema,
//                              String schemaRegistryUrl,
//                              int identityMapCapacity,
//                              Class<T> recordClazz) {
//        super(applicationName, alerter, checkpointConfiguration,
//            kafkaBrokers, kafkaSourceTopic, offsetResetStrategy, autoCommit, consumerGroupId, additionalConsumerConfig);
//        this.schema = schema;
//        this.schemaRegistryUrl = schemaRegistryUrl;
//        this.identityMapCapacity = identityMapCapacity;
//        this.recordClazz = recordClazz;
//    }

    public AvroKafkaStreaming(AvroKafkaStreamingConfig streamingConfig,
                              Alerter alerter,
                              Schema schema,
                              String schemaRegistryUrl,
                              int identityMapCapacity,
                              Class<T> recordClazz) throws Exception {
        super(streamingConfig, alerter);
        this.schema = schema;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.identityMapCapacity = identityMapCapacity;
        this.recordClazz = recordClazz;
        this.schemaCoderProvider = new CachedSchemaCoderProvider(kafkaSourceTopic, schemaRegistryUrl, identityMapCapacity);
    }

    FlinkKafkaConsumer<T> getFlinkKafkaConsumer() {
        ExtendedRegistryAvroDeserializationSchema<T> deserializationSchema = new ExtendedRegistryAvroDeserializationSchema<T>(recordClazz, schema, schemaCoderProvider);
        return getFlinkKafkaConsumer(deserializationSchema);
    }

    public void start() throws Exception {
        start(getFlinkKafkaConsumer());
    }
}
