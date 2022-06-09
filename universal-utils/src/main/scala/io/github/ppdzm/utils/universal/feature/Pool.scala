package io.github.ppdzm.utils.universal.feature

import java.util.Properties

import io.github.ppdzm.utils.universal.base.LoggingTrait
import io.github.ppdzm.utils.universal.encryption.PasswordBasedEncryptor
import org.apache.commons.pool2.ObjectPool

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by Stuart Alex on 2021/2/25.
 *
 */
object Pool {
    def borrow[T, R](resource: ObjectPool[T])(f: T => R): R = {
        val o = resource.borrowObject()
        try {
            f(o)
        } catch {
            case e: Throwable => throw e
        } finally {
            resource.returnObject(o)
        }
    }
}

trait Pool[T] extends LoggingTrait{
    protected val _pool: mutable.Map[String, ObjectPool[T]] = mutable.Map[String, ObjectPool[T]]()
    sys.addShutdownHook {
        this._pool.values.foreach(_.close())
    }

    def getKey(properties: Properties): String = {
        PasswordBasedEncryptor.md5Digest {
            properties
                .keys()
                .map(key => (key.toString, properties.get(key).toString))
                .toList
                .sortBy(_._1)
                .map(e => e._1 + ":" + e._2)
                .mkString(",")

        }
    }
}
