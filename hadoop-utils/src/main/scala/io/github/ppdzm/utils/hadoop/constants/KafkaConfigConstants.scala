package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.ConfigItem

trait KafkaConfigConstants extends ConfigTrait {
    lazy val KAFKA_BROKERS: ConfigItem = ConfigItem("kafka.brokers")
    lazy val KAFKA_CONSUMER_POLL_MS: ConfigItem = ConfigItem("kafka.consumer.poll.ms", 5000)
    lazy val KAFKA_GROUP_ID: ConfigItem = ConfigItem("kafka.group.id")
    lazy val KAFKA_BACKPRESSURE_ENABLED: ConfigItem = ConfigItem("kafka.backpressure.enabled", true)
    lazy val KAFKA_MAX_RATE_PER_PARTITION: ConfigItem = ConfigItem("kafka.maxRatePerPartition", 1000)
    lazy val KAFKA_OFFSET_CONFIG: ConfigItem = ConfigItem("kafka.offset.config")
    lazy val KAFKA_TOPICS: ConfigItem = ConfigItem("kafka.topics")
    lazy val KAFKA_MANUAL_COMMIT_ENABLED: ConfigItem = ConfigItem("kafka.manual.commit.enabled", true)
}
