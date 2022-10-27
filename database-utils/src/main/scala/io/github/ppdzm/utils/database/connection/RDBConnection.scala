package io.github.ppdzm.utils.database.connection

import java.sql.{Connection, PreparedStatement, Statement}

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait RDBConnection {

    def getConnection(url: String, properties: Map[String, AnyRef]): Connection

    def close(): Unit
}
