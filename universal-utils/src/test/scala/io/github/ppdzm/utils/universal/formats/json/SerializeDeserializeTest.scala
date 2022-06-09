package io.github.ppdzm.utils.universal.formats.json

import io.github.ppdzm.utils.universal.base.Person
import org.json4s.native.Serialization
import org.json4s.{Formats, NoTypeHints}
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/4/1.
 */
class SerializeDeserializeTest extends FunSuite {

    test("serialize") {
        val cc = CaseClass("Stuart Alex", 30)
        println(Json4sUtils.serialize4s(cc))
        val sc = new ScalaClass("Stuart Alex", 30)
        println(Json4sUtils.serialize4s(sc))
        val person = new Person("Stuart Alex", 30)
        println(JsonUtils.serialize(person))
    }

    test("deserialize") {
        implicit val formats: Formats = Serialization.formats(NoTypeHints)
        val json = """{"name":"Stuart Alex","age":30}"""
        val cc = Serialization.read[CaseClass](json)
        println(cc)
        val sc = Json4sUtils.deserialize4s[ScalaClass](json)
        println(sc)
        val person = JsonUtils.deserialize(json, classOf[Person])
        println(person)
    }

    case class CaseClass(name: String, age: Int)

    class ScalaClass(val name: String, val age: Int)

}