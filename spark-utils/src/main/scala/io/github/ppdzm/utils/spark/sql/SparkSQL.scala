package io.github.ppdzm.utils.spark.sql

import io.github.ppdzm.utils.spark.SparkUtils
import org.apache.spark.sql.DataFrame

/**
 * Created by Stuart Alex on 2016/5/26.
 */
object SparkSQL {
    private lazy val sparkSession = SparkUtils.getSparkSession()

    def mysql: SparkMySQLUtils.type = SparkMySQLUtils

    def sqlServer: SparkSQLServerUtils.type = SparkSQLServerUtils

    def sql(sql: String): DataFrame = sparkSession.sql(sql)

}
