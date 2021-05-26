package io.github.ppdzm.utils.flink.streaming.kafka;

import io.github.ppdzm.utils.flink.streaming.kafka.config.KafkaStreamingConfig;
import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class AvroKafkaStreamingConfig extends KafkaStreamingConfig {
    public ConfigItem AVRO_SCHEMA_REGISTRY_URL;

    public AvroKafkaStreamingConfig(String applicationName, Config config) {
        super(applicationName, config);
        this.AVRO_SCHEMA_REGISTRY_URL = new ConfigItem(config, "avro.schema-registry.hosts");
    }
}
