package io.github.ppdzm.utils.universal.base

import java.util

import io.github.ppdzm.utils.universal.cli.{CliUtils, Render}
import io.github.ppdzm.utils.universal.core.SystemProperties
import org.slf4j.{Logger, LoggerFactory}

/**
 * @author Created by Stuart Alex on 2021/6/16.
 */
trait LoggingTrait {
    val logger: Logger = LoggerFactory.getLogger(getClass)

    def setLogging2Stdout(enabled: Boolean): Unit = {
        SystemProperties.setLogging2Stdout(enabled)
    }

    protected def logDebug(message: String, renders: Render*): Unit = {
        val renderedMessage = Logging.rendering(message, renders: _*)
        if (SystemProperties.getLogging2Stdout) {
            println(renderedMessage)
        } else {
            logger.debug(renderedMessage)
        }
    }

    def logDebug(`object`: Any, renders: Render*): Unit = {
        logDebug(`object`.toString, renders: _*)
    }

    protected def logInfo(message: String, renders: Render*): Unit = {
        val renderedMessage = Logging.rendering(message, renders: _*)
        if (SystemProperties.getLogging2Stdout) {
            println(renderedMessage)
        } else {
            logger.info(renderedMessage)
        }
    }

    def logInfo(`object`: Any, renders: Render*): Unit = {
        logInfo(`object`.toString, renders: _*)
    }

    protected def logInfo(messages: util.List[String], messageRenders: util.Map[String, Render]): Unit = {
        val renderedMessage = CliUtils.rendering(messages, messageRenders)
        if (SystemProperties.getLogging2Stdout) {
            println(renderedMessage)
        } else {
            logger.info(renderedMessage)
        }
    }

    def logWarning(message: String, renders: Render*): Unit = {
        val renderedMessage = Logging.rendering(message, renders: _*)
        if (SystemProperties.getLogging2Stdout) {
            println(renderedMessage)
        } else {
            logger.warn(renderedMessage)
        }
    }

    def logWarning(`object`: Any, renders: Render*): Unit = {
        logWarning(`object`.toString, renders: _*)
    }

    protected def logError(message: String, renders: Render*): Unit = {
        val renderedMessage = Logging.rendering(message, renders: _*)
        if (SystemProperties.getLogging2Stdout) {
            println(renderedMessage)
        } else {
            logger.error(renderedMessage)
        }
    }
}
