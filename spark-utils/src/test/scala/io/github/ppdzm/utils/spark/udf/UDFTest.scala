package io.github.ppdzm.utils.spark.udf

import org.sa.utils.spark.SparkUtils
import org.scalatest.FunSuite

class UDFTest extends FunSuite {

    test("list") {
        val spark = SparkUtils.getSparkSession()
        spark.sql("show functions").show()
    }

}
