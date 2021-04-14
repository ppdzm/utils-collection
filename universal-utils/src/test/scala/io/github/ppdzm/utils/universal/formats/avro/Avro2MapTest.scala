package io.github.ppdzm.utils.universal.formats.avro

import java.io.File

import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.sa.utils.universal.feature.LoanPattern
import org.scalatest.FunSuite

import scala.collection.JavaConversions._
import scala.io.Source

/**
 * Created by Stuart Alex on 2021/3/4.
 */
class Avro2MapTest extends FunSuite {

    test("avro2map") {
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        new File("../data/json/log").listFiles().foreach {
            file =>
                val json = LoanPattern.using(Source.fromFile(file, "utf-8")) { bs => bs.mkString }
                val genericRecordArray = AvroUtils.json2Avro[GenericData.Array[GenericRecord]](json, schema)
                genericRecordArray.flatten.foreach {
                    record =>
                        val map = AvroUtils.parseRecord2Map(record, reserveParentName = true)
                        println(map.mkString(","))
                }
        }
    }

}
