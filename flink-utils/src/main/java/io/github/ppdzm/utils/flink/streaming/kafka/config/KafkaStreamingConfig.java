package io.github.ppdzm.utils.flink.streaming.kafka.config;

import io.github.ppdzm.utils.flink.streaming.StreamingConfig;
import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Map;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class KafkaStreamingConfig extends StreamingConfig {
    public ConfigItem SOURCE_KAFKA_BROKERS;
    public ConfigItem SOURCE_KAFKA_TOPIC;
    public ConfigItem SOURCE_KAFKA_CONSUMER_GROUP_ID;
    public ConfigItem SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT;
    public ConfigItem SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG;
    public ConfigItem SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG;

    public KafkaStreamingConfig(String applicationName, Config config) {
        super(applicationName, config);
        this.SOURCE_KAFKA_BROKERS = new ConfigItem(config, "source.kafka.brokers");
        this.SOURCE_KAFKA_TOPIC = new ConfigItem(config, "source.kafka.topic");
        this.SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT = new ConfigItem(config, "source.kafka.consumer.enable.auto.commit", true);
        this.SOURCE_KAFKA_CONSUMER_GROUP_ID = new ConfigItem(config, "source.kafka.consumer.group-id");
        this.SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG = new ConfigItem(config, "source.kafka.consumer.offset.config", "earliest");
        this.SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG = new ConfigItem(config, "source.kafka.consumer.additional.config", "");
    }

    public OffsetResetStrategy getOffsetResetStrategy() throws Exception {
        return OffsetResetStrategy.valueOf(SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG.stringValue().toUpperCase());
    }

    public Properties getConsumerProperties() throws Exception {
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", this.SOURCE_KAFKA_BROKERS.stringValue());
        consumerProperties.put("auto.offset.reset", this.SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG.stringValue());
        consumerProperties.put("enable.auto.commit", this.SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT.booleanValue());
        consumerProperties.put("group.id", this.SOURCE_KAFKA_CONSUMER_GROUP_ID.stringValue());
        Map<String, String> additionalConsumerConfig = this.SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG.mapValue();
        if (additionalConsumerConfig != null) {
            for (String key : additionalConsumerConfig.keySet()) {
                String value = additionalConsumerConfig.get(key);
                consumerProperties.put(key, value);
            }
        }
        return consumerProperties;
    }

}
