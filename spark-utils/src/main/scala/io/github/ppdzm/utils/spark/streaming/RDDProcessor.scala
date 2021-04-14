package io.github.ppdzm.utils.spark.streaming

import org.apache.spark.rdd.RDD

trait RDDProcessor[S, R] extends Serializable {
    /**
     * RDD处理逻辑
     *
     * @param rdd RDD[T]
     */
    def processRDD(rdd: RDD[S]): RDD[R]
}
