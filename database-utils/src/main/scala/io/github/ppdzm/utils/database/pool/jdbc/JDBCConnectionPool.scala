package io.github.ppdzm.utils.database.pool.jdbc

import java.sql.Connection

import io.github.ppdzm.utils.universal.base.LoggingTrait
import scalikejdbc.ConnectionPool

/**
 * Created by Stuart Alex on 2017/3/29.
 */
abstract class JDBCConnectionPool extends LoggingTrait {

    /**
     * 通过name从连接池中获取一个ConnectionPool
     *
     * @param name 连接池中唯一标识一个ConnectionPool的名称
     * @return scalikejdbc.ConnectionPool
     */
    def get(name: Symbol): ConnectionPool = ConnectionPool(name)

    /**
     * 通过name从连接池中获取一个Connection
     *
     * @param name 连接池中唯一标识一个ConnectionPool的名称
     * @return java.sql.Connection
     */
    def borrow(name: Symbol): Connection = ConnectionPool(name).borrow

}