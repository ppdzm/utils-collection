package io.github.ppdzm.utils.universal.formats.avro


import java.io.File
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.scalatest.FunSuite
import scalikejdbc.LoanPattern

import scala.collection.JavaConversions._
import scala.io.Source

/**
 * Created by Stuart Alex on 2021/3/4.
 */
class Avro2MapTest extends FunSuite {

    test("avro2map") {
        val schema = new Schema.Parser().parse(new File("../data/avsc/schema-all.json"))
        val file = new File("../data/json/wechat.json")
        val json = LoanPattern.using(Source.fromFile(file, "utf-8")) { bs => bs.mkString }
        val genericRecordArray = AvroUtils.json2Avro[GenericData.Array[GenericRecord]](json, schema)
        genericRecordArray
            .flatten
            .foreach {
                record =>
                    val map = AvroUtils.parseRecord2Map(record, true)
                    map.foreach {
                        case (key, value) =>
                            println(key + " => " + String.valueOf(value))
                    }
            }
    }

}
