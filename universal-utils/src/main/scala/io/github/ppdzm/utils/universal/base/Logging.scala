package io.github.ppdzm.utils.universal.base

import io.github.ppdzm.utils.universal.cli.{CliUtils, Render, ScalaCliUtils}
import io.github.ppdzm.utils.universal.core.{CoreConstants, SystemProperties}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.LoggerExtensions._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by Stuart Alex on 2017/12/26.
 */
object Logging {
    def setLogging2Stdout(boolean: Boolean): String = {
        SystemProperties.set(CoreConstants.LOGGING_STDOUT_ENABLED_KEY, boolean.toString)
    }
}

trait Logging extends Serializable {
    private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)
    private lazy val standardOut = SystemProperties.getLogging2Stdout

    protected def logDebug(message: => String, renders: Render*): Unit = {
        if (standardOut)
            println(CliUtils.rendering(message, renders: _*))
        else
            logger.logDebug(message, renders: _*)
    }

    protected def logInfo(message: => String, renders: Render*): Unit = {
        if (standardOut) {
            if (renders.isEmpty)
                println(message.green)
            else
                println(CliUtils.rendering(message, renders: _*))
        } else
            logger.logInfo(message, renders: _*)
    }

    protected def logInfo(messages: Array[(String, Render)]): Unit = {
        if (standardOut)
            println(ScalaCliUtils.rendering(messages))
        else
            logger.logInfo(messages)
    }

    protected def logWarning(message: => String, renders: Render*): Unit = {
        if (standardOut) {
            if (renders.isEmpty)
                println(message.yellow)
            else
                println(CliUtils.rendering(message, renders: _*))
        } else
            logger.logWarning(message, renders: _*)
    }

    protected def logError(message: => String, renders: Render*): Unit = {
        if (standardOut) {
            if (renders.isEmpty)
                println(message.red)
            else
                println(CliUtils.rendering(message, renders: _*))
        } else
            logger.logError(message, renders: _*)
    }

    protected def logError(message: => String, throwable: Throwable): Unit = {
        logger.logError(message, throwable)
    }

}
