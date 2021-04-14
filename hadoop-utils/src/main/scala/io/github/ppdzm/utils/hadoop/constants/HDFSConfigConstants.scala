package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.ConfigItem

trait HDFSConfigConstants extends ConfigTrait {
    lazy val HDFS_ENABLED: ConfigItem = ConfigItem("hdfs.enabled", true)
    lazy val HDFS_HA: ConfigItem = ConfigItem("hdfs.ha", true)
    /**
     * 未启用hdfs高可用时，使用此项配置
     */
    lazy val HDFS_NAMENODE_ADDRESS: ConfigItem = ConfigItem("hdfs.namenode.address")
    /**
     * 启用hdfs高可用时，使用此项配置
     */
    lazy val HDFS_NAMENODES: ConfigItem = ConfigItem("hdfs.namenodes")
    lazy val HDFS_NAMESERVICE: ConfigItem = ConfigItem("hdfs.nameservice")
    lazy val HDFS_PORT: ConfigItem = ConfigItem("hdfs.port", 8020)
}
