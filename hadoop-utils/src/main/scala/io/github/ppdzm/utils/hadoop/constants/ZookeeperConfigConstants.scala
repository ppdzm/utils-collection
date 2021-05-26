package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait ZookeeperConfigConstants extends ConfigTrait {
    lazy val ZOOKEEPER_CONNECTION: ConfigItem = new ConfigItem(config, "zookeeper.connection")
    lazy val ZOOKEEPER_PORT: ConfigItem = new ConfigItem(config, "zookeeper.port", 2181)
    lazy val ZOOKEEPER_QUORUM: ConfigItem = new ConfigItem(config, "zookeeper.quorum")
}
