package io.github.ppdzm.utils.flink.streaming.kafka;

import io.github.ppdzm.utils.flink.streaming.kafka.config.AvroFlinkKafkaStreamingConfig;
import io.github.ppdzm.utils.universal.alert.Alerter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AvroRecordFlinkKafkaStreaming extends AvroFlinkKafkaStreaming<GenericRecord> {

//    public AvroRecordFlinkKafkaStreaming(String applicationName,
//                                    Alerter alerter,
//                                    CheckpointConfigItems checkpointConfiguration,
//                                    String kafkaBrokers,
//                                    String kafkaSourceTopic,
//                                    OffsetResetStrategy offsetResetStrategy,
//                                    boolean autoCommit,
//                                    String consumerGroupId,
//                                    Map<String, String> additionalConsumerConfig,
//                                    Schema schema,
//                                    String schemaRegistryUrl,
//                                    int identityMapCapacity) {
//        super(applicationName, alerter, checkpointConfiguration,
//            kafkaBrokers, kafkaSourceTopic, offsetResetStrategy, autoCommit, consumerGroupId, additionalConsumerConfig,
//            schema, schemaRegistryUrl, identityMapCapacity, GenericRecord.class);
//    }

    public AvroRecordFlinkKafkaStreaming(AvroFlinkKafkaStreamingConfig streamingConfig,
                                         Alerter alerter,
                                         Schema schema,
                                         String schemaRegistryUrl,
                                         int identityMapCapacity) throws Exception {
        super(streamingConfig, alerter, schema, schemaRegistryUrl, identityMapCapacity, GenericRecord.class);
    }

}
