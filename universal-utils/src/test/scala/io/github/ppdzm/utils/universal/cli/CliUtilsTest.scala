package io.github.ppdzm.utils.universal.cli

import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.core.CoreConstants
import io.github.ppdzm.utils.universal.formats.json.Json4sUtils
import org.scalatest.FunSuite

import java.util
import java.util.Properties
import scala.util.Try

/**
 * @author StuartAlex on 2019-07-26 18:18
 */
class CliUtilsTest extends FunSuite {

    test("renders") {
        println(Json4sUtils.serialize4s(Render.valueOf(0)))
        println(Json4sUtils.serialize4s(Render.valueOf(32)))
        val failure = Try(Json4sUtils.serialize4s(Render.valueOf(100)))
        assert(failure.isFailure)
        println(failure.failed.get.getMessage)
    }

    test("args parser test") {
        CliUtils.parseArguments("--a=1 --b 2 --c --d e".split(" "), new Properties())
        CliUtils.parseArguments("--a 1 --b --c d e --f g".split(" "), new Properties())
        CliUtils.parseArguments("--a 1 --b --c ".split(" "), new Properties())
    }

    test("render") {
        System.setProperty(CoreConstants.LOGGING_STDOUT_ENABLED_KEY, "true")
        val logging = new Logging(getClass);
        logging.logInfo("Receive args: " + CliUtils.rendering(String.join(", ", util.Arrays.asList("a", "b")), Render.GREEN))
    }

}
