package io.github.ppdzm.utils.spark.udaf

import org.sa.utils.spark.SparkUtils
import org.sa.utils.spark.file.SparkFile
import org.sa.utils.spark.sql.SparkSQL
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/5/1.
 */
class UDAFTest extends FunSuite {

    test("collect as list") {
        val sparkSession = SparkUtils.getSparkSession()
        sparkSession.udf.register("cal", new CollectAsList)
        val df = SparkFile.df("files/example.csv")
        df.createOrReplaceTempView("some")
        df.show()
        SparkSQL.sql("select age,cal(name) from some group by age").show()
    }

}