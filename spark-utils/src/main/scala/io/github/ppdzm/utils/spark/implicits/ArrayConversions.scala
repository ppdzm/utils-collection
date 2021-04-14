package io.github.ppdzm.utils.spark.implicits

import org.apache.spark.rdd.RDD
import io.github.ppdzm.utils.spark.SparkUtils

import scala.reflect.ClassTag

object ArrayConversions {

    implicit class ArrayImplicits1[T: ClassTag](array: Array[T]) {

        /**
         * Array并行化
         *
         * @return
         */
        def parallelize(slices: Int = 1): RDD[T] = SparkUtils.getSparkSession().sparkContext.parallelize(array, slices)
    }

}
