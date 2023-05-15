package io.github.ppdzm.utils.hadoop.scala.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait KafkaConfigConstants extends ConfigTrait {
    lazy val KAFKA_BROKERS: ConfigItem = new ConfigItem(config, "kafka.brokers")
    lazy val KAFKA_CONSUMER_POLL_MS: ConfigItem = new ConfigItem(config, "kafka.consumer.poll.ms", 5000)
    lazy val KAFKA_GROUP_ID: ConfigItem = new ConfigItem(config, "kafka.group.id")
    lazy val KAFKA_BACKPRESSURE_ENABLED: ConfigItem = new ConfigItem(config, "kafka.backpressure.enabled", true)
    lazy val KAFKA_MAX_RATE_PER_PARTITION: ConfigItem = new ConfigItem(config, "kafka.maxRatePerPartition", 1000)
    lazy val KAFKA_OFFSET_CONFIG: ConfigItem = new ConfigItem(config, "kafka.offset.config")
    lazy val KAFKA_TOPICS: ConfigItem = new ConfigItem(config, "kafka.topics")
    lazy val KAFKA_MANUAL_COMMIT_ENABLED: ConfigItem = new ConfigItem(config, "kafka.manual.commit.enabled", true)
}
