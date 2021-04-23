package io.github.ppdzm.utils.database.connection

import java.sql.{Connection, PreparedStatement, Statement}
import java.util.Properties

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait RDBConnection {

    def getConnection(url: String, properties: Map[String, AnyRef]): Connection

    def getStatement(url: String, properties: Map[String, AnyRef]): Statement

    def getPreparedStatement(url: String, properties: Map[String, AnyRef], sql: String): PreparedStatement

    def close(): Unit
}
