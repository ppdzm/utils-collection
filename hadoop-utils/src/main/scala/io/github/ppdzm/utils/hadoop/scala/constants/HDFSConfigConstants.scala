package io.github.ppdzm.utils.hadoop.scala.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait HDFSConfigConstants extends ConfigTrait {
    lazy val HDFS_ENABLED: ConfigItem = new ConfigItem(config, "hdfs.enabled", true)
    lazy val HDFS_HA: ConfigItem = new ConfigItem(config, "hdfs.ha", true)
    /**
     * 未启用hdfs高可用时，使用此项配置
     */
    lazy val HDFS_NAMENODE_ADDRESS: ConfigItem = new ConfigItem(config, "hdfs.namenode.address")
    /**
     * 启用hdfs高可用时，使用此项配置
     */
    lazy val HDFS_NAMENODES: ConfigItem = new ConfigItem(config, "hdfs.namenodes")
    lazy val HDFS_NAMESERVICE: ConfigItem = new ConfigItem(config, "hdfs.nameservice")
    lazy val HDFS_PORT: ConfigItem = new ConfigItem(config, "hdfs.port", 8020)
}
