package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.cli.Renders
import io.github.ppdzm.utils.universal.implicits.LoggerExtensions._
import org.scalatest.FunSuite
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by Stuart Alex on 2021/4/6.
 */
class LoggerExtensionTest extends FunSuite {

    test("logging") {
        val logger: Logger = LoggerFactory.getLogger(this.getClass)
        logger.logInfo("aaa")
        logger.logInfo("bbb", Renders.RED)
    }

}
