package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.cli.{CliUtils, Render}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

import scala.collection.JavaConverters._

object LoggerExtensions {

    implicit class Log4jImplicits(logger: org.apache.log4j.Logger) {

        def logDebug(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.debug(message.green)
            else
                logger.debug(CliUtils.rendering(message, renders: _*))
        }

        def logInfo(messages: Array[(String, Render)]): Unit = {
            logger.logInfo(CliUtils.rendering(messages.toMap.asJava))
        }

        def logInfo(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.info(message.green)
            else
                logger.info(CliUtils.rendering(message, renders: _*))
        }

        def logWarning(messages: Array[(String, Render)]): Unit = {
            logger.logWarning(CliUtils.rendering(messages.toMap.asJava))
        }

        def logWarning(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.warn(CliUtils.rendering(message, Render.YELLOW))
            else
                logger.warn(CliUtils.rendering(message, renders: _*))
        }

        def logError(messages: Array[(String, Render)]): Unit = {
            logger.logError(CliUtils.rendering(messages.toMap.asJava))
        }

        def logError(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.error(message.red)
            else
                logger.error(CliUtils.rendering(message, renders: _*))
        }

        def logError(message: => String, throwable: Throwable): Unit = {
            logger.error(message, throwable)
        }
    }

    implicit class Slf4jImplicits(logger: org.slf4j.Logger) {

        def logDebug(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.debug(message.green)
            else
                logger.debug(CliUtils.rendering(message, renders: _*))
        }

        def logInfo(messages: Array[(String, Render)]): Unit = {
            logger.logInfo(CliUtils.rendering(messages.toMap.asJava))
        }

        def logInfo(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.info(message.green)
            else
                logger.info(CliUtils.rendering(message, renders: _*))
        }

        def logWarning(messages: Array[(String, Render)]): Unit = {
            logger.logWarning(CliUtils.rendering(messages.toMap.asJava))
        }

        def logWarning(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.warn(CliUtils.rendering(message, Render.YELLOW))
            else
                logger.warn(CliUtils.rendering(message, renders: _*))
        }

        def logError(messages: Array[(String, Render)]): Unit = {
            logger.logError(CliUtils.rendering(messages.toMap.asJava))
        }

        def logError(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.error(message.red)
            else
                logger.error(CliUtils.rendering(message, renders: _*))
        }

        def logError(message: => String, throwable: Throwable): Unit = {
            logger.error(message, throwable)
        }
    }

}
