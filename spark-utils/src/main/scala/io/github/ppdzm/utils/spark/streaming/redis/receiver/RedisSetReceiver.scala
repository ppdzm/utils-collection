package io.github.ppdzm.utils.spark.streaming.redis.receiver

import io.github.ppdzm.utils.spark.streaming.redis.wrapper.JedisWrapper
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.spark.storage.StorageLevel

/**
 * Created by Stuart Alex on 2017/4/6.
 */
class RedisSetReceiver(keySet: Set[String], storageLevel: StorageLevel) extends RedisReceiver(keySet, storageLevel) {
    override protected val config: Config = new FileConfig()

    override def getData(j: JedisWrapper, key: String): String = j.spop(key)
}