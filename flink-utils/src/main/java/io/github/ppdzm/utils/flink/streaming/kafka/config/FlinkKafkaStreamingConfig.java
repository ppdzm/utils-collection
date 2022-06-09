package io.github.ppdzm.utils.flink.streaming.kafka.config;

import io.github.ppdzm.utils.flink.streaming.config.FlinkStreamingConfig;
import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class FlinkKafkaStreamingConfig extends FlinkStreamingConfig {
    public ConfigItem SOURCE_KAFKA_BROKERS;
    public ConfigItem SOURCE_KAFKA_TOPICS;
    public ConfigItem SOURCE_KAFKA_CONSUMER_GROUP_ID;
    public ConfigItem SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT;
    public ConfigItem SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG;
    public ConfigItem SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG;

    public FlinkKafkaStreamingConfig(String applicationName, Config config) {
        super(applicationName, config);
        this.SOURCE_KAFKA_BROKERS = new ConfigItem(config, "source.kafka.brokers");
        this.SOURCE_KAFKA_TOPICS = new ConfigItem(config, "source.kafka.topics");
        this.SOURCE_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT = new ConfigItem(config, "source.kafka.consumer.enable.auto.commit", true);
        this.SOURCE_KAFKA_CONSUMER_GROUP_ID = new ConfigItem(config, "source.kafka.consumer.group-id");
        this.SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG = new ConfigItem(config, "source.kafka.consumer.offset.config", "earliest");
        this.SOURCE_KAFKA_CONSUMER_ADDITIONAL_CONFIG = new ConfigItem(config, "source.kafka.consumer.additional.config", "");
    }

    public OffsetResetStrategy getOffsetResetStrategy() throws Exception {
        return OffsetResetStrategy.valueOf(SOURCE_KAFKA_CONSUMER_OFFSET_CONFIG.stringValue().toUpperCase());
    }

}
