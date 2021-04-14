package io.github.ppdzm.utils.universal.base

import io.github.ppdzm.utils.universal.cli.Renders.Render
import org.sa.utils.universal.cli.Renders.Render
import org.sa.utils.universal.implicits.LoggerExtensions._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by Stuart Alex on 2017/12/26.
 */
trait Logging extends Serializable {
    private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

    protected def logDebug(message: => String, renders: Render*): Unit = {
        logger.logDebug(message, renders: _*)
    }

    protected def logInfo(message: => String, renders: Render*): Unit = {
        logger.logInfo(message, renders: _*)
    }

    protected def logInfo(messages: Array[(String, Render)]): Unit = {
        logger.logInfo(messages)
    }

    protected def logWarning(message: => String, renders: Render*): Unit = {
        logger.logWarning(message, renders: _*)
    }

    protected def logError(message: => String, renders: Render*): Unit = {
        logger.logError(message, renders: _*)
    }

    protected def logError(message: => String, throwable: Throwable): Unit = {
        logger.logError(message, throwable)
    }

}
