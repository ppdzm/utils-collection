package io.github.ppdzm.utils.universal.feature

object SyntaxSugar {

    implicit class TripleExpression(expression: => Boolean) {
        def ?(ifValue: => Any): (Boolean, () => Any) = (expression, () => ifValue)
    }

    implicit class TripleExpressionElse[T](elseValue: => T) {
        def `:`(undetermined: (Boolean, () => Any)): T = {
            if (undetermined._1)
                undetermined._2.apply().asInstanceOf[T]
            else
                elseValue
        }
    }

    @annotation.tailrec
    def `try`[T](tryTimes: Int, sleepMillis: Int = 1)(fn: => T): T = {
        util.Try {
            fn
        } match {
            case util.Success(x) => x
            case _ if tryTimes > 1 => Thread.sleep(sleepMillis); `try`(tryTimes - 1)(fn)
            case util.Failure(e) => throw e
        }
    }

}
