package io.github.ppdzm.utils.hadoop.scala.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait HBaseConfigConstants extends ConfigTrait {
    lazy val HBASE_MASTER: ConfigItem = new ConfigItem(config, "hbase.master")
}
