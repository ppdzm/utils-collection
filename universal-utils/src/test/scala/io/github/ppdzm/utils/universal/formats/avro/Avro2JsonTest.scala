package io.github.ppdzm.utils.universal.formats.avro

import java.io.File
import java.nio.charset.StandardCharsets._
import java.util.Base64
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.luben.zstd.Zstd
import io.github.ppdzm.utils.universal.formats.json.JacksonJsonUtils
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite

import scala.collection.JavaConversions._

class Avro2JsonTest extends FunSuite {

    test("integration-test") {
        val schema = AvroUtils.getSchemaFromFile("../data/avsc/schema-all.json")
        val file = new File("../data/json/wechat/10.25.21.70_3029_1335.json.raw.txt")
        val bytesArray = FileUtils.readLines(file, UTF_8)
            .map(fix)
            .map { line => AvroUtils.json2AvroBytesWithoutSchema(line, schema) }
            .map { avroBytes => Base64.getEncoder.encode(Zstd.compress(avroBytes)) }
            .toArray
        val head = bytesArray.head
        val tail = Array.concat[Byte](bytesArray.tail.map { bytes => Array.concat(System.lineSeparator().getBytes(UTF_8), bytes) }: _*)
        val concatBytes = Array.concat(head, tail)
        FileUtils.writeByteArrayToFile(new File("../data/avro/10.25.21.70_3029_1335.json.avro.zstd.b64"), concatBytes)
        FileUtils.readLines(new File("../data/avro/10.25.21.70_3029_1335.json.avro.zstd.b64"), UTF_8).foreach {
            line =>
                val zstdCompressed = Base64.getDecoder.decode(line)
                val avroBytes = Zstd.decompress(zstdCompressed, zstdCompressed.length * 2)
                val objects = AvroUtils.bytes2Object(avroBytes, schema)
                objects.foreach(println)
                println("--" * 100)
        }
        new File("../data/avro")
            .listFiles()
            .filter(_.getName.endsWith("b64"))
            .foreach {
                file =>
                    FileUtils.readLines(file, UTF_8).foreach {
                        line =>
                            val zstdCompressed = Base64.getDecoder.decode(line)
                            val avroBytes = Zstd.decompress(zstdCompressed, zstdCompressed.length * 2)
                            val objects = AvroUtils.bytes2Object(avroBytes, schema)
                            objects.foreach(println)
                            println("--" * 100)
                    }
            }
    }

    def fix(singleLog: String): String = {
        JacksonJsonUtils.parse(singleLog)
            .asInstanceOf[ArrayNode]
            .map(node => JacksonJsonUtils.serialize(node))
            .map {
                compacted =>
                    compacted.replace("\"browse_time\":\"\"", "\"browse_time\":null")
                        .replace("\"page_X\":\"\"", "\"page_X\":null")
                        .replace("\"page_Y\":\"\"", "\"page_Y\":null")
                        .replace("\"client_X\":\"\"", "\"client_X\":null")
                        .replace("\"client_Y\":\"\"", "\"client_Y\":null")
            }
            .map(cleared => s"""{"wechat.wechat":$cleared}""")
            .mkString("[", ",", "]")
    }

}
