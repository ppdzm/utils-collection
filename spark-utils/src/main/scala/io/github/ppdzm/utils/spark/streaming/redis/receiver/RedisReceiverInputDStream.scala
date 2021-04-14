package io.github.ppdzm.utils.spark.streaming.redis.receiver

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream

/**
 * Created by Stuart Alex on 2017/4/6.
 */
case class RedisReceiverInputDStream(streamingContext: StreamingContext, struct: String, keySet: Set[String]) extends ReceiverInputDStream[(String, String)](streamingContext) {
    private val _storageLevel = StorageLevel.MEMORY_AND_DISK_SER_2

    override def getReceiver(): RedisReceiver = {
        struct match {
            case "list" => new RedisListReceiver(keySet, _storageLevel)
            case "set" => new RedisSetReceiver(keySet, _storageLevel)
            case _ => throw new IllegalArgumentException("Unsupported Redis Structure. The only supported structure is (1)list or (2)set")
        }
    }

}