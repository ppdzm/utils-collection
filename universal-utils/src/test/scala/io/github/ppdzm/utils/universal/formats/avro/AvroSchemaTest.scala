package io.github.ppdzm.utils.universal.formats.avro

import java.io.File

import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.avro.Schema
import org.scalatest.FunSuite

import scala.collection.JavaConversions._

class AvroSchemaTest extends FunSuite {
    test("simple") {
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        schema.getType.getName match {
            case "array" =>
                schema.getElementType.getType.getName match {
                    case "union" =>
                }
            case "record" =>
                schema.getFields.foreach {
                    field =>
                        field.schema().getType.getName match {
                            case "union" =>
                                println(field.name() + s":${if (field.schema().getName == "union") s"Option[String]" else field.schema().getTypes.last.getName.toCamel}")
                            case "record" =>
                                println(field.name() + ":")
                                field.schema().getFields.foreach {
                                    f => println("\t" + f)
                                }
                            case e => println(field.name() + ":" + e.toPascal)
                        }
                }
        }
    }

}
