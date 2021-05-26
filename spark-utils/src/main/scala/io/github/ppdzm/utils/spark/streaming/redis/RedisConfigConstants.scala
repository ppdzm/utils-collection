package io.github.ppdzm.utils.spark.streaming.redis

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait RedisConfigConstants extends ConfigTrait {
    lazy val REDIS_CLUSTER_ENABLED: ConfigItem = new ConfigItem(config, "redis.cluster.enabled", false)
    lazy val REDIS_HOST: ConfigItem = new ConfigItem(config, "redis.host", "localhost")
    lazy val REDIS_KEY_SET: ConfigItem = new ConfigItem(config, "redis.keyset")
    lazy val REDIS_PORT: ConfigItem = new ConfigItem(config, "redis.port", 6379)
    lazy val REDIS_STRUCT: ConfigItem = new ConfigItem(config, "redis.struct", "list")
    lazy val REDIS_TIMEOUT: ConfigItem = new ConfigItem(config, "redis.timeout", 200)
}
