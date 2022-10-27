package io.github.ppdzm.utils.database.pool.mysql

import io.github.ppdzm.utils.database.handler.MySQLHandler
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool

/**
 * Created by Stuart Alex on 2021/2/20.
 */
object MySQLHandlerPool extends Pool[MySQLHandler] {

    def apply(url: String, properties: Map[String, AnyRef] = Map()): ObjectPool[MySQLHandler] = {
        this._pool.getOrElse(url, {
            this.logging.logInfo(s"MySQLHandler with url $url does not exists, create it and add it into MySQLHandler Pool")
            synchronized[ObjectPool[MySQLHandler]] {
                val pool =
                    if (properties == null)
                        new GenericObjectPool[MySQLHandler](new MySQLHandlerFactory(url, Map()))
                    else
                        new GenericObjectPool[MySQLHandler](new MySQLHandlerFactory(url, properties))
                pool.addObject()
                this._pool.put(url, pool)
                pool
            }
        })
    }
}
