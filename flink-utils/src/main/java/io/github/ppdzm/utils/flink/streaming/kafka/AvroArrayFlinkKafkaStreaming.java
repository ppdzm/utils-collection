package io.github.ppdzm.utils.flink.streaming.kafka;

import io.github.ppdzm.utils.flink.streaming.kafka.config.AvroFlinkKafkaStreamingConfig;
import io.github.ppdzm.utils.universal.alert.Alerter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AvroArrayFlinkKafkaStreaming extends AvroFlinkKafkaStreaming<GenericData.Array> {

//    public AvroArrayFlinkKafkaStreaming(String applicationName,
//                                   Alerter alerter,
//                                   CheckpointConfigItems checkpointConfiguration,
//                                   String kafkaBrokers,
//                                   String kafkaSourceTopic,
//                                   OffsetResetStrategy offsetResetStrategy,
//                                   boolean autoCommit,
//                                   String consumerGroupId,
//                                   Map<String, String> additionalConsumerConfig,
//                                   Schema schema,
//                                   String schemaRegistryUrl,
//                                   int identityMapCapacity) {
//        super(applicationName, alerter, checkpointConfiguration,
//            kafkaBrokers, kafkaSourceTopic, offsetResetStrategy, autoCommit, consumerGroupId, additionalConsumerConfig,
//            schema, schemaRegistryUrl, identityMapCapacity, GenericData.Array.class);
//    }

    public AvroArrayFlinkKafkaStreaming(AvroFlinkKafkaStreamingConfig streamingConfig,
                                        Alerter alerter,
                                        Schema schema,
                                        int identityMapCapacity) throws Exception {
        super(streamingConfig, alerter, schema, streamingConfig.AVRO_SCHEMA_REGISTRY_URL.stringValue(), identityMapCapacity, GenericData.Array.class);
    }
}
