package io.github.ppdzm.utils.hadoop.scala.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait YarnConfigConstants extends ConfigTrait {
    lazy val RESOURCE_MANAGER_ADDRESS: ConfigItem = new ConfigItem(config, "yarn.rm.address")
}
