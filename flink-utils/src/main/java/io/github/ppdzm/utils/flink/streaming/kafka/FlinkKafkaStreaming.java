package io.github.ppdzm.utils.flink.streaming.kafka;

import io.github.ppdzm.utils.flink.common.CheckpointConfiguration;
import io.github.ppdzm.utils.flink.streaming.FlinkStreaming;
import io.github.ppdzm.utils.flink.streaming.config.FlinkStreamingConfig;
import io.github.ppdzm.utils.flink.streaming.kafka.config.FlinkKafkaStreamingConfig;
import io.github.ppdzm.utils.universal.alert.Alerter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class FlinkKafkaStreaming<T> extends FlinkStreaming<T> {
    private static final long serialVersionUID = -3419202161966039599L;
    /**
     * Kafka源Brokers
     */
    private String kafkaSourceBrokers;
    /**
     * Kafka源Topic（列表）
     */
    protected List<String> kafkaSourceTopics;
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

    public FlinkKafkaStreaming(String applicationName,
                               Alerter alerter,
                               CheckpointConfiguration checkpointConfiguration,
                               String kafkaSourceBrokers,
                               List<String> kafkaSourceTopics,
                               OffsetResetStrategy offsetResetStrategy,
                               boolean autoCommit,
                               String consumerGroupId,
                               @Nullable Map<String, String> additionalConsumerConfig) {
        super(applicationName, alerter, checkpointConfiguration, new FlinkStreamingConfig());
        this.kafkaSourceBrokers = kafkaSourceBrokers;
        this.kafkaSourceTopics = kafkaSourceTopics;
        this.offsetResetStrategy = offsetResetStrategy;
        this.autoCommit = autoCommit;
        this.consumerGroupId = consumerGroupId;
        this.additionalConsumerConfig = additionalConsumerConfig;
    }

    public FlinkKafkaStreaming(FlinkKafkaStreamingConfig streamingConfig, Alerter alerter) throws Exception {
        super(streamingConfig, alerter);
        this.kafkaSourceBrokers = streamingConfig.SOURCE_KAFKA_BROKERS.stringValue();
        this.kafkaSourceTopics = Arrays.asList(streamingConfig.SOURCE_KAFKA_TOPICS.arrayValue());
        this.offsetResetStrategy = streamingConfig.getOffsetResetStrategy();
        this.autoCommit = streamingConfig.SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT.booleanValue();
        this.consumerGroupId = streamingConfig.SOURCE_KAFKA_CONSUMER_GROUP_ID.stringValue();
        this.additionalConsumerConfig = streamingConfig.SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG.mapValue();
    }

    protected FlinkKafkaConsumer<T> getFlinkKafkaConsumer(DeserializationSchema<T> deserializationSchema) {
        Properties consumerProperties = getConsumerProperties();
        FlinkKafkaConsumer<T> dataSource = new FlinkKafkaConsumer<>(this.kafkaSourceTopics, deserializationSchema, consumerProperties);
        dataSource.setStartFromGroupOffsets();
        dataSource.setCommitOffsetsOnCheckpoints(true);
        return dataSource;
    }

    public void start(DeserializationSchema<T> deserializationSchema) throws Exception {
        start(getFlinkKafkaConsumer(deserializationSchema));
    }

    protected FlinkKafkaConsumer<T> getFlinkKafkaConsumer(KafkaDeserializationSchema<T> kafkaDeserializationSchema) {
        Properties consumerProperties = getConsumerProperties();
        FlinkKafkaConsumer<T> dataSource = new FlinkKafkaConsumer<>(this.kafkaSourceTopics, kafkaDeserializationSchema, consumerProperties);
        dataSource.setStartFromGroupOffsets();
        dataSource.setCommitOffsetsOnCheckpoints(true);
        return dataSource;
    }

    public void start(KafkaDeserializationSchema<T> kafkaDeserializationSchema) throws Exception {
        start(getFlinkKafkaConsumer(kafkaDeserializationSchema));
    }

    public Properties getConsumerProperties() {
        Properties consumerProperties = new Properties();
        consumerProperties.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaSourceBrokers);
        consumerProperties.put(AUTO_OFFSET_RESET_CONFIG, this.offsetResetStrategy.toString().toLowerCase());
        consumerProperties.put(ENABLE_AUTO_COMMIT_CONFIG, this.autoCommit);
        consumerProperties.put(GROUP_ID_CONFIG, this.consumerGroupId);
        if (additionalConsumerConfig != null) {
            for (String key : additionalConsumerConfig.keySet()) {
                String value = additionalConsumerConfig.get(key);
                consumerProperties.put(key, value);
            }
        }
        return consumerProperties;
    }

}
