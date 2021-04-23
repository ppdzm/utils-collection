package io.github.ppdzm.utils.database.connection

import java.sql.{Connection, PreparedStatement, ResultSet, Statement}
import java.util.Properties

import io.github.ppdzm.utils.database.common.{DatabaseConstants, Drivers}
import io.github.ppdzm.utils.database.pool.jdbc.HikariConnectionPool

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * @author StuartAlex on 2019-07-26 14:55
 */
object MySQLConnection extends RDBConnection {
    Drivers.MySQL.load()
    private val defaultProperties: Properties = DatabaseConstants.mySQLDefaultProperties
    private val statements = mutable.HashMap[String, Statement]()
    private val prepareStatements = mutable.HashMap[String, PreparedStatement]()

    override def getStatement(url: String, properties: Map[String, AnyRef]): Statement = {
        properties.foreach {
            case (k, v) => defaultProperties.put(k, v)
        }
        if (!statements.contains(url) || statements(url).isClosed) {
            val connection = getConnection(url, defaultProperties.toMap)
            val statement = connection.createStatement
            statements.put(url, statement)
        }
        statements(url)
    }

    override def getPreparedStatement(url: String, properties: Map[String, AnyRef], sql: String): PreparedStatement = {
        val key = url + sql
        if (properties != null) {
            properties.foreach {
                case (k, v) => defaultProperties.put(k, v)
            }
        }
        if (!prepareStatements.contains(key) || prepareStatements(key).isClosed) {
            val connection = getConnection(url, defaultProperties.toMap)
            val preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT)
            preparedStatement.executeQuery("SET NAMES utf8mb4")
            prepareStatements.put(key, preparedStatement)
        }
        prepareStatements(key)
    }

    override def getConnection(url: String, properties: Map[String, AnyRef]): Connection = {
        HikariConnectionPool(jdbcUrl = url, properties = properties).borrow()
    }

    override def close(): Unit = {
        statements.values.foreach(_.close())
        prepareStatements.values.foreach(_.close())
        prepareStatements.values.foreach(_.close())
    }

}
