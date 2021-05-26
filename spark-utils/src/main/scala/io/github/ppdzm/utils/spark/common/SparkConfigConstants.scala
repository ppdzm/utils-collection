package io.github.ppdzm.utils.spark.common

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

private[common] trait SparkConfigConstants extends ConfigTrait {
    protected lazy val SPARK_APP_NAME: ConfigItem = new ConfigItem(config, "spark.app.name", "spark-application")
    protected lazy val SPARK_MASTER: ConfigItem = new ConfigItem(config, "spark.master", "local[*]")
}
