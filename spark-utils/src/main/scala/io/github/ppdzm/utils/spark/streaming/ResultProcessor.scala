package io.github.ppdzm.utils.spark.streaming

/**
 * Created by Stuart Alex on 2016/11/25.
 * Kafka某个topic中各个partition的处理器
 */
trait ResultProcessor[R] extends Serializable {
    /**
     * Partition处理逻辑
     *
     * @param result Iterator[T]
     */
    def processResult(result: R): Unit

}
