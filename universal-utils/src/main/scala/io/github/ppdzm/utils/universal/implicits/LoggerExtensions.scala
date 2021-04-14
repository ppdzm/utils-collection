package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.cli.{CliUtils, Renders}
import io.github.ppdzm.utils.universal.cli.Renders.Render
import org.sa.utils.universal.cli.Renders.Render
import org.sa.utils.universal.cli.{CliUtils, Renders}
import org.sa.utils.universal.implicits.BasicConversions._

object LoggerExtensions {

    implicit class Log4jImplicits(logger: org.apache.log4j.Logger) {

        def logDebug(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.debug(message.green)
            else
                logger.debug(CliUtils.render(message, renders: _*))
        }

        def logInfo(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.info(message.green)
            else
                logger.info(CliUtils.render(message, renders: _*))
        }

        def logInfo(messages: Array[(String, Render)]): Unit = {
            logger.logInfo(CliUtils.render(messages))
        }

        def logWarning(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.warn(CliUtils.render(message, Renders.YELLOW))
            else
                logger.warn(CliUtils.render(message, renders: _*))
        }

        def logWarning(messages: Array[(String, Render)]): Unit = {
            logger.logWarning(CliUtils.render(messages))
        }

        def logError(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.error(message.red)
            else
                logger.error(CliUtils.render(message, renders: _*))
        }

        def logError(messages: Array[(String, Render)]): Unit = {
            logger.logError(CliUtils.render(messages))
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
                logger.debug(CliUtils.render(message, renders: _*))
        }

        def logInfo(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.info(message.green)
            else
                logger.info(CliUtils.render(message, renders: _*))
        }

        def logInfo(messages: Array[(String, Render)]): Unit = {
            logger.logInfo(CliUtils.render(messages))
        }

        def logWarning(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.warn(CliUtils.render(message, Renders.YELLOW))
            else
                logger.warn(CliUtils.render(message, renders: _*))
        }

        def logWarning(messages: Array[(String, Render)]): Unit = {
            logger.logWarning(CliUtils.render(messages))
        }

        def logError(message: => String, renders: Render*): Unit = {
            if (renders.isEmpty)
                logger.error(message.red)
            else
                logger.error(CliUtils.render(message, renders: _*))
        }

        def logError(messages: Array[(String, Render)]): Unit = {
            logger.logError(CliUtils.render(messages))
        }

        def logError(message: => String, throwable: Throwable): Unit = {
            logger.error(message, throwable)
        }
    }

}
