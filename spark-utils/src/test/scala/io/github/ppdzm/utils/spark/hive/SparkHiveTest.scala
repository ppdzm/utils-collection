package io.github.ppdzm.utils.spark.hive

import org.scalatest.FunSuite

/**
 * Created by yuqitao on 2017/4/5.
 */
class SparkHiveTest extends FunSuite {

    test("createTable") {
        val database = "himtest"
        SparkHiveUtils.createDatabase(database)
    }
    test("createExternalHiveOverHBaseTable4Query") {
        println(SparkHiveUtils.createExternalHiveOverHBaseTableFromTableSchema("default", "hive_test", "default", "hive", "hbase", ("info", "ROW_DATA_FINAL_STATUS")))
    }

}