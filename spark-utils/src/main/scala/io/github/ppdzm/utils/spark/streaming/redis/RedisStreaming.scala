package io.github.ppdzm.utils.spark.streaming.redis

import io.github.ppdzm.utils.spark.common.SparkStreamingEnvironment
import io.github.ppdzm.utils.spark.streaming.redis.receiver.RedisReceiverInputDStream
import io.github.ppdzm.utils.spark.streaming.{PartitionProcessor, RDDProcessor, ResultProcessor}

/**
 * Created by Stuart Alex on 2017/4/5.
 */
abstract class RedisStreaming[R1, R2] extends RedisConfigConstants with SparkStreamingEnvironment with RDDProcessor[(String, String), R1] {
    private lazy val keySet = REDIS_KEY_SET.arrayValue(",").toSet
    private lazy val struct = REDIS_STRUCT.stringValue
    override protected val intervalInSeconds: Int = SPARK_STREAMING_SECONDS.intValue

    def startWithProcessor(partitionProcessor: PartitionProcessor[R1, R2], resultProcessor: ResultProcessor[R2]): Unit = {
        startWithProcessFunction(partitionProcessor.processPartition, resultProcessor.processResult)
    }

    def startWithProcessFunction(partitionProcessFunction: Iterator[R1] => R2, resultProcessFunction: R2 => Unit): Unit = {
        RedisReceiverInputDStream(streamingContext, this.struct, this.keySet)
            .foreachRDD {
                rdd =>
                    this.processRDD(rdd).foreachPartition {
                        partition =>
                            val result = partitionProcessFunction(partition)
                            resultProcessFunction(result)
                    }
            }
        streamingContext.start()
        streamingContext.awaitTermination()
    }

}