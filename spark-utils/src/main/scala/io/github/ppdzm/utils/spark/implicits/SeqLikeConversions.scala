package io.github.ppdzm.utils.spark.implicits

import io.github.ppdzm.utils.spark.SparkUtils
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object SeqLikeConversions {

    implicit class SeqImplicits1[T: ClassTag](seq: Seq[T]) {

        /**
         * List并行化
         *
         * @return
         */
        def parallelize(slices: Int = 1): RDD[T] = SparkUtils.getSparkSession().sparkContext.parallelize(seq, slices)
    }

}
