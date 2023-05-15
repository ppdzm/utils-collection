package io.github.ppdzm.utils.hadoop.scala.kafka.functions

import io.github.ppdzm.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2021/1/29.
 */
trait ExceptionHandler {
    protected lazy val logging = new Logging(getClass)

    def handle(e: Exception, data: Any)

}

object PrintExceptionHandler extends ExceptionHandler {
    override def handle(e: Exception, data: Any): Unit = {
        this.logging.logger.error(data.toString, e)
    }
}

object ExitExceptionHandler extends ExceptionHandler {
    override def handle(e: Exception, data: Any): Unit = {
        this.logging.logger.error(data.toString, e)
        sys.exit(-1)
    }
}

object ThrowExceptionHandler extends ExceptionHandler {
    override def handle(e: Exception, data: Any): Unit = {
        this.logging.logger.error(data.toString, e)
        throw e
    }
}