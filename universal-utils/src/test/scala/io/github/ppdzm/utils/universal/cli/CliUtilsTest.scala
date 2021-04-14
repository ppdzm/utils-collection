package io.github.ppdzm.utils.universal.cli

import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import org.scalatest.FunSuite

import scala.util.Try

/**
 * @author StuartAlex on 2019-07-26 18:18
 */
class CliUtilsTest extends FunSuite {

    test("renders") {
        println(JsonUtils.serialize4s(Renders.valueOf(0)))
        println(JsonUtils.serialize4s(Renders.valueOf(32)))
        val failure = Try(JsonUtils.serialize4s(Renders.valueOf(100)))
        assert(failure.isFailure)
        println(failure.failed.get.getMessage)
    }

    test("args parser test") {
        CliUtils.parseArguments("--a=1 --b 2 --c --d e".split(" "))
        CliUtils.parseArguments("--a 1 --b --c d e --f g".split(" "))
        CliUtils.parseArguments("--a 1 --b --c ".split(" "))
    }

}
