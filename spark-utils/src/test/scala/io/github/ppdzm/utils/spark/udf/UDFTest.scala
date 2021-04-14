package io.github.ppdzm.utils.spark.udf

import io.github.ppdzm.utils.spark.SparkUtils
import org.scalatest.FunSuite

class UDFTest extends FunSuite {

    test("list") {
        val spark = SparkUtils.getSparkSession()
        spark.sql("show functions").show()
    }

}
