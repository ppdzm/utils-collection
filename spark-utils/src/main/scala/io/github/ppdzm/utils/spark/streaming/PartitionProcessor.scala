package io.github.ppdzm.utils.spark.streaming

/**
 * Created by Stuart Alex on 2016/11/25.
 * Kafka某个topic中各个partition的处理器
 */
trait PartitionProcessor[S, R] extends Serializable {
    /**
     * Partition处理逻辑
     *
     * @param partition Iterator[T]
     */
    def processPartition(partition: Iterator[S]): R

}
