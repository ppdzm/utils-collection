package io.github.ppdzm.utils.spark.streaming.redis

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.ConfigItem

trait RedisConfigConstants extends ConfigTrait {
    lazy val REDIS_CLUSTER_ENABLED: ConfigItem = ConfigItem("redis.cluster.enabled", false)
    lazy val REDIS_HOST: ConfigItem = ConfigItem("redis.host", "localhost")
    lazy val REDIS_KEY_SET: ConfigItem = ConfigItem("redis.keyset")
    lazy val REDIS_PORT: ConfigItem = ConfigItem("redis.port", 6379)
    lazy val REDIS_STRUCT: ConfigItem = ConfigItem("redis.struct", "list")
    lazy val REDIS_TIMEOUT: ConfigItem = ConfigItem("redis.timeout", 200)
}
