package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.ConfigItem

trait YarnConfigConstants extends ConfigTrait {
    lazy val RESOURCE_MANAGER_ADDRESS: ConfigItem = ConfigItem("yarn.rm.address")
}
