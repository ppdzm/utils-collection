package io.github.ppdzm.utils.database.connection

import io.github.ppdzm.utils.database.common.{DatabaseConstants, Drivers}
import io.github.ppdzm.utils.database.pool.jdbc.HikariConnectionPool

import java.sql.{Connection, PreparedStatement, ResultSet, Statement}
import java.util.Properties
import scala.collection.JavaConversions._

/**
 * @author StuartAlex on 2019-07-26 14:55
 */
object MySQLConnection extends RDBConnection {
    Drivers.MySQL.load()
    private val defaultProperties: Properties = DatabaseConstants.mySQLDefaultProperties

    override def getConnection(url: String, properties: Map[String, AnyRef]): Connection = {
        if (properties != null) {
            defaultProperties.foreach {
                case (k, v) =>
                    if (!properties.contains(k))
                        defaultProperties.put(k, v)
            }
            HikariConnectionPool(jdbcUrl = url, properties = properties).borrow()
        } else {
            HikariConnectionPool(jdbcUrl = url, properties = defaultProperties.toMap).borrow()
        }
    }

    override def close(): Unit = {
    }

}
