package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.ConfigItem

trait HBaseConfigConstants extends ConfigTrait {
    lazy val HBASE_MASTER: ConfigItem = ConfigItem("hbase.master")
}
