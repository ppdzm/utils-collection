package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.base.ExceptionUtils
import io.github.ppdzm.utils.universal.finance.Loan.PaybackDetails
import io.github.ppdzm.utils.universal.implicits.BasicConversions.StringImplicits
import io.github.ppdzm.utils.universal.implicits.SeqConversions._

import java.util.Properties
import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2021/3/30.
 */
object ExtendedJavaConversions {

    implicit class PaybackDetailsImplicits(paybackDetails: PaybackDetails) {

        def display(render: String = "0;32"): Unit = {
            s"贷款${paybackDetails.principle}元，月利率${paybackDetails.rate}".prettyPrintln(render)
            s"总还款额${paybackDetails.totalPayback}元，总利息${paybackDetails.totalInterest}元".prettyPrintln(render)
            "还款详情如下：".prettyPrintln(render)
            paybackDetails
              .monthPaybackDetails
              .map { e => List(e.period.toString, e.payback.toString, e.principle.toString, e.interest.toString) }
              .prettyShow(render, -1, false, 0, 0, true, true, true, false, 0, true, List("Period", "Month Payback", "Principle", "Interest"))
        }

    }

    implicit class PropertiesImplicits(properties: Properties) {

        def toKeyValuePair: Array[(String, String)] = {
            properties.keySet().map(key => key.toString -> properties.get(key).toString).toArray
        }

    }

    implicit class ThrowableImplicits(throwable: Throwable) {
        def toDetailedString: String = {
            ExceptionUtils.exceptionToString(throwable)
        }
    }

}
