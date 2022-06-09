package io.github.ppdzm.utils.database.pool.mysql

import io.github.ppdzm.utils.database.handler.MySQLHandler
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}

/**
 * Created by Stuart Alex on 2021/2/20.
 */
class MySQLHandlerFactory(url: String, properties: Map[String, AnyRef]) extends BasePooledObjectFactory[MySQLHandler] {
    override def create(): MySQLHandler = MySQLHandler(url, properties)

    override def wrap(t: MySQLHandler): PooledObject[MySQLHandler] = new DefaultPooledObject[MySQLHandler](t)
}
