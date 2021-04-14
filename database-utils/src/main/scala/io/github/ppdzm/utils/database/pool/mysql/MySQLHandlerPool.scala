package io.github.ppdzm.utils.database.pool.mysql

import io.github.ppdzm.utils.database.handler.MySQLHandler
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.sa.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2021/2/20.
 */
object MySQLHandlerPool extends Pool[MySQLHandler] with Logging {

    def apply(url: String, properties: Map[String, AnyRef]): ObjectPool[MySQLHandler] = {
        this._pool.getOrElse(url, {
            this.logInfo(s"MySQLHandler with url $url does not exists, create it and add it into MySQLHandler Pool")
            synchronized[ObjectPool[MySQLHandler]] {
                val pool = new GenericObjectPool[MySQLHandler](new MySQLHandlerFactory(url, properties))
                pool.addObject()
                this._pool.put(url, pool)
                pool
            }
        })
    }
}
