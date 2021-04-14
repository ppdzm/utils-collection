package io.github.ppdzm.utils.universal.formats.avro

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Base64

import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.luben.zstd.Zstd
import io.github.ppdzm.utils.universal.feature.LoanPattern
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.commons.io.FileUtils
import org.sa.utils.universal.feature.LoanPattern
import org.sa.utils.universal.formats.json.JsonUtils
import org.scalatest.FunSuite

import scala.collection.JavaConversions._
import scala.io.Source

class Json2AvroTest extends FunSuite {

    test("wechat-json-log-2-avro-generic-data") {
        val originalJsonString = LoanPattern.using(Source.fromFile(new File("../data/json/original/log-wechat-click.json"), "utf-8"))(s => s.mkString)
        val jsonArray = JsonUtils.parse(originalJsonString)
        println("compacted original json string is:")
        val compactedOriginalJsonString = JsonUtils.serialize(jsonArray)
        println("original size " + compactedOriginalJsonString.getBytes.length + " " + compactedOriginalJsonString)
        val repairedJsonString = jsonArray.map(node => s"""{"wechat.wechat":${JsonUtils.serialize(node)}}""").mkString("[", ",", "]")
        println("repaired json string is:")
        println("repaired size " + repairedJsonString.getBytes.length + " " + repairedJsonString)
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        println("schema is:")
        println(schema)
        val avroList = AvroUtils.json2Avro[GenericData.Array[_]](repairedJsonString + repairedJsonString, schema)
        avroList.foreach {
            avro =>
                println(avro.getClass)
                println(avro.toString)
        }
    }

    test("wechat-json-log-2-avro-bytes") {
        val originalJsonString = LoanPattern.using(Source.fromFile(new File("../data/json/original/log-wechat-click.json"), "utf-8"))(s => s.mkString)
        val jsonArray = JsonUtils.parse(originalJsonString)
        println("compacted original json string is:")
        val compactedOriginalJsonString = JsonUtils.serialize(jsonArray)
        println("original size " + compactedOriginalJsonString.getBytes.length + " " + compactedOriginalJsonString)
        val repairedJsonString = jsonArray.map(node => s"""{"wechat.wechat":${JsonUtils.serialize(node)}}""").mkString("[", ",", "]")
        println("repaired json string is:")
        println("repaired size " + repairedJsonString.getBytes.length + " " + repairedJsonString)
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        println("schema is:")
        println(schema)
        // 两个加在一起的json也可以
        val avroBytes = AvroUtils.json2AvroBytesWithoutSchema(repairedJsonString, schema)
        println("avro size " + avroBytes.length)
        println()
        println(new String(avroBytes))
        println()
        val zstdCompressedBytes = Zstd.compress(avroBytes)
        println("zstd compressed size " + zstdCompressedBytes.length)
        val base64edBytes = Base64.getEncoder.encode(zstdCompressedBytes)
        println("base64 encoded after zstd compressed size " + base64edBytes.length)
        println(new String(base64edBytes))
        val dataArray = AvroUtils.bytes2Object(avroBytes, schema)
        println("json string from avro bytes is:")
        dataArray.foreach(println)
    }

    test("wechat-serial-log") {
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        Array("click", "cold_start", "hot_start", "use_end", "view_page")
            .foreach {
                e =>
                    val file = new File(s"../data/json/log/log-wechat-$e.json")
                    println(file.getName)
                    val jsonString = LoanPattern.using(Source.fromFile(file, "utf-8"))(s => s.mkString)
                    val jsonArray = JsonUtils.parse(jsonString).asInstanceOf[ArrayNode]
                    jsonArray.foreach {
                        node =>
                            node.get("events").asInstanceOf[ArrayNode].foreach {
                                eventNode =>
                                    println(eventNode.get(eventNode.fieldNames().next()).get("event_type").asText())
                            }
                    }
                    val repairedJsonString = jsonArray.map {
                        node => s"""{"wechat.wechat":${JsonUtils.serialize(node)}}"""
                    }
                        .mkString("[", ",", "]")
                    println(repairedJsonString)
                    println("repaired json string size is " + repairedJsonString.getBytes.length)
                    val avroBytes = AvroUtils.json2AvroBytesWithoutSchema(repairedJsonString, schema)
                    println("avro bytes size is " + avroBytes.length)
                    println(new String(avroBytes))
                    val zstdCompressedBytes = Zstd.compress(avroBytes)
                    println("zstd compressed bytes size is " + zstdCompressedBytes.length)
                    val base64 = Base64.getEncoder.encodeToString(zstdCompressedBytes)
                    println("base64 encoded bytes size is " + base64.getBytes.length)
                    FileUtils.write(new File(s"../data/avro/wechat-$e.avro.zstd.b64"), base64, StandardCharsets.UTF_8)
                    FileUtils.write(new File(s"../data/avro/wechat-all.avro.zstd.b64"), base64, StandardCharsets.UTF_8, true)
                    FileUtils.write(new File(s"../data/avro/all.avro.zstd.b64"), System.lineSeparator(), StandardCharsets.UTF_8, true)
                    AvroUtils.bytes2Generic[Object](avroBytes).foreach(println)
                    println("=" * 100)
            }
    }

    test("json 2 avro schema") {
        println(AvroUtils.parseSchemaFromJson("""{"coordinate_info":{"page_X":290,"page_Y":51,"client_X":290,"client_Y":51}}""", "aaa", "bbb"))
    }

    test("h5-in-ios") {
        val schema = AvroUtils.getSchema("../data/avsc/schema-all.json")
        val content = FileUtils.readLines(new File("../data/json/ios/h5-in-ios.json"), StandardCharsets.UTF_8).mkString
        val avro = AvroUtils.json2AvroBytesWithoutSchema(content, schema)
        println(new String(avro))
        println("-" * 100)
        val zstdCompressed = Zstd.compress(avro)
        val b64Encoded = Base64.getEncoder.encode(zstdCompressed)
        println(new String(b64Encoded))
        val b64Decoded = Base64.getDecoder.decode(b64Encoded)
        val zstdDecompressed = Zstd.decompress(b64Decoded, b64Decoded.length * 50)
        val json = AvroUtils.bytes2Object(zstdDecompressed, schema)
        println("-" * 100)
        json.foreach(println)
    }

}
