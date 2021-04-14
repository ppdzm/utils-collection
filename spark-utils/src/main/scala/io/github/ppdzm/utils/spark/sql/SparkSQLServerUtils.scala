package io.github.ppdzm.utils.spark.sql

import java.sql.ResultSet
import java.util.Properties

import io.github.ppdzm.utils.database.common.Drivers
import io.github.ppdzm.utils.database.pool.jdbc.HikariConnectionPool
import org.apache.spark.sql.DataFrame
import io.github.ppdzm.utils.database.common.Drivers
import io.github.ppdzm.utils.spark.SparkUtils

object SparkSQLServerUtils {

    def df(url: String, user: String, password: String, table: String): DataFrame = {
        val properties = new Properties
        properties.put("username", user)
        properties.put("password", password)
        properties.put("useunicode", "true")
        properties.put("characterEncoding", "utf8")
        SparkUtils.getSparkSession().read.jdbc(url, table, properties)
    }

    def rs(url: String, user: String, password: String, statement: String, parameters: String*): ResultSet = {
        val conn = this.connection(url, user, password)
        val ps = conn.prepareStatement(statement, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT)
        if (parameters != null)
            parameters.indices.filter(parameters(_) != null).foreach(i => ps.setObject(i + 1, parameters(i)))
        ps.executeQuery()
    }

    private def connection(url: String, user: String, password: String) = {
        HikariConnectionPool(driver = Drivers.SQLServer.toString, jdbcUrl = url).borrow()
    }
}
