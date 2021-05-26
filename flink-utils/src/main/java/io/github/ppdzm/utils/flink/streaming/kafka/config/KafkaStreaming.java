package io.github.ppdzm.utils.flink.streaming.kafka.config;

import io.github.ppdzm.utils.flink.common.CheckpointConfiguration;
import io.github.ppdzm.utils.flink.streaming.Streaming;
import io.github.ppdzm.utils.flink.streaming.StreamingConfig;
import io.github.ppdzm.utils.universal.alert.Alerter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class KafkaStreaming<T> extends Streaming<T> {
    private static final long serialVersionUID = -3419202161966039599L;
    /**
     * Kafka源Brokers
     */
    private String kafkaSourceBrokers;
    /**
     * Kafka源Topic
     */
    protected String kafkaSourceTopic;
    /**
     * Kafka消费者OffsetResetStrategy
     */
    private OffsetResetStrategy offsetResetStrategy;
    /**
     * Kafka消费者是否自动提交
     */
    private boolean autoCommit;
    /**
     * Kafka消费者GROUP ID
     */
    private String consumerGroupId;
    /**
     * Kafka消费者附加配置
     */
    private Map<String, String> additionalConsumerConfig;

    public KafkaStreaming(String applicationName,
                          Alerter alerter,
                          CheckpointConfiguration checkpointConfiguration,
                          String kafkaSourceBrokers,
                          String kafkaSourceTopic,
                          OffsetResetStrategy offsetResetStrategy,
                          boolean autoCommit,
                          String consumerGroupId,
                          @Nullable Map<String, String> additionalConsumerConfig) {
        super(applicationName, alerter, checkpointConfiguration, new StreamingConfig());
        this.kafkaSourceBrokers = kafkaSourceBrokers;
        this.kafkaSourceTopic = kafkaSourceTopic;
        this.offsetResetStrategy = offsetResetStrategy;
        this.autoCommit = autoCommit;
        this.consumerGroupId = consumerGroupId;
        this.additionalConsumerConfig = additionalConsumerConfig;
    }

    public KafkaStreaming(KafkaStreamingConfig streamingConfig, Alerter alerter) throws Exception {
        super(streamingConfig, alerter);
        this.kafkaSourceBrokers = streamingConfig.SOURCE_KAFKA_BROKERS.stringValue();
        this.kafkaSourceTopic = streamingConfig.SOURCE_KAFKA_TOPIC.stringValue();
        this.offsetResetStrategy = streamingConfig.getOffsetResetStrategy();
        this.autoCommit = streamingConfig.SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT.booleanValue();
        this.consumerGroupId = streamingConfig.SOURCE_KAFKA_CONSUMER_GROUP_ID.stringValue();
        this.additionalConsumerConfig = streamingConfig.SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG.mapValue();
    }

    protected FlinkKafkaConsumer<T> getFlinkKafkaConsumer(DeserializationSchema<T> deserializationSchema) {
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", kafkaSourceBrokers);
        consumerProperties.put("auto.offset.reset", offsetResetStrategy.toString().toLowerCase());
        consumerProperties.put("enable.auto.commit", autoCommit);
        consumerProperties.put("group.id", consumerGroupId);
        if (additionalConsumerConfig != null) {
            for (String key : additionalConsumerConfig.keySet()) {
                String value = additionalConsumerConfig.get(key);
                consumerProperties.put(key, value);
            }
        }
        FlinkKafkaConsumer<T> dataSource = new FlinkKafkaConsumer<>(kafkaSourceTopic, deserializationSchema, consumerProperties);
        dataSource.setStartFromGroupOffsets();
        dataSource.setCommitOffsetsOnCheckpoints(true);
        return dataSource;
    }

    public void start(DeserializationSchema<T> deserializationSchema) throws Exception {
        start(getFlinkKafkaConsumer(deserializationSchema));
    }

}
