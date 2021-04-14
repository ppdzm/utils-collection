package io.github.ppdzm.utils.hadoop.kafka.functions

import io.github.ppdzm.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2021/1/29.
 */
trait ExceptionHandler {

    def handle(e: Exception, data: Any)

}

object PrintExceptionHandler extends ExceptionHandler with Logging {
    override def handle(e: Exception, data: Any): Unit = {
        logError(data.toString, e)
    }
}

object ExitExceptionHandler extends ExceptionHandler with Logging {
    override def handle(e: Exception, data: Any): Unit = {
        logError(data.toString, e)
        sys.exit(-1)
    }
}

object ThrowExceptionHandler extends ExceptionHandler with Logging {
    override def handle(e: Exception, data: Any): Unit = {
        logError(data.toString, e)
        throw e
    }
}