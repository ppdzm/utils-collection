package io.github.ppdzm.utils.universal.cli

import java.util.Properties

import io.github.ppdzm.utils.universal.formats.json.ScalaJsonUtils
import org.scalatest.FunSuite

import scala.util.Try

/**
 * @author StuartAlex on 2019-07-26 18:18
 */
class CliUtilsTest extends FunSuite {

    test("renders") {
        println(ScalaJsonUtils.serialize4s(Render.valueOf(0)))
        println(ScalaJsonUtils.serialize4s(Render.valueOf(32)))
        val failure = Try(ScalaJsonUtils.serialize4s(Render.valueOf(100)))
        assert(failure.isFailure)
        println(failure.failed.get.getMessage)
    }

    test("args parser test") {
        CliUtils.parseArguments("--a=1 --b 2 --c --d e".split(" "), new Properties())
        CliUtils.parseArguments("--a 1 --b --c d e --f g".split(" "), new Properties())
        CliUtils.parseArguments("--a 1 --b --c ".split(" "), new Properties())
    }

}
