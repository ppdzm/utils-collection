package io.github.ppdzm.utils.spark.streaming.redis.wrapper

/**
 * Created by Stuart Alex on 2017/4/5.
 */
trait JedisWrapper {

    def spop(key: String): String

    def lpop(key: String): String

    def close(): Unit

}