package io.github.ppdzm.utils.database.pool.jdbc

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.github.ppdzm.utils.database.common.Drivers
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}

/**
 * Created by Stuart Alex on 2017/3/27.
 */
object HikariConnectionPool extends JDBCConnectionPool {

    /**
     * 通过name尝试从连接池中获取ConnectionPool，未找到则创建一个
     *
     * @param identity   连接池中唯一标识一个ConnectionPool的名称
     * @param driver     驱动名称
     * @param jdbcUrl    jdbc连接字符串
     * @param username   用户名
     * @param password   密码
     * @param properties 额外设置的一些属性
     * @return scalikejdbc.ConnectionPool
     */
    def apply(identity: String = null, driver: String = Drivers.MySQL.toString, jdbcUrl: String,
              username: String = null, password: String = null, properties: Map[String, AnyRef] = Map()): ConnectionPool = {
        val name = if (identity != null) Symbol(identity) else Symbol(jdbcUrl)
        if (!ConnectionPool.isInitialized(name)) {
            this.logInfo(s"ConnectionPool named ${name} does not exists, create it with url $jdbcUrl and add it into HikariConnectionPool")
            val hikariConfig = new HikariConfig()
            hikariConfig.setDriverClassName(driver.toString)
            hikariConfig.setJdbcUrl(jdbcUrl)
            if (username != null)
                hikariConfig.setUsername(username)
            if (password != null)
                hikariConfig.setPassword(password)
            hikariConfig.setIdleTimeout(60000)
            hikariConfig.setConnectionTimeout(60000)
            hikariConfig.setValidationTimeout(3000)
            hikariConfig.setMaxLifetime(60000)
            hikariConfig.setMaximumPoolSize(10)
            properties.foreach(property => hikariConfig.addDataSourceProperty(property._1, property._2))
            val hikariDataSource = new HikariDataSource(hikariConfig)
            hikariDataSource.setLoginTimeout(5)
            ConnectionPool.add(name, new DataSourceConnectionPool(hikariDataSource))
        }
        get(name)
    }

}
