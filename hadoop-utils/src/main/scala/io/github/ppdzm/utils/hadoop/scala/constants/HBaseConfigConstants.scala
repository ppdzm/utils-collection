package io.github.ppdzm.utils.hadoop.scala.constants

import io.github.ppdzm.utils.universal.config.{ConfigHolder, ConfigItem}

trait HBaseConfigConstants extends ConfigHolder {
    lazy val HBASE_MASTER: ConfigItem = new ConfigItem(config, "hbase.master")
}
