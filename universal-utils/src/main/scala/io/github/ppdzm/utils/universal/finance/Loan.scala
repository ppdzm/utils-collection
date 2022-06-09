package io.github.ppdzm.utils.universal.finance

import io.github.ppdzm.utils.universal.base.Mathematics
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.SeqConversions._

/**
 * Created by Stuart Alex on 2017/9/22.
 */
object Loan extends App {
    println(rate(72000, 6360, 12) * 12)
//    println(rate(12000, 1090, 12) * 12)

    /**
     * 等额本金还款详情
     *
     * @param principal 本金
     * @param rate      月利率
     * @param periods   还款期数
     * @return
     */
    def averageCapital(principal: Double, rate: Double, periods: Int): PaybackDetails = {
        var restAmount = principal
        var interestSum = 0.0
        val details = (0 until periods).map(i => {
            val principalPayback = Mathematics.round(principal / periods, 2)
            val interest = Mathematics.round(restAmount * rate, 2)
            restAmount -= principalPayback
            interestSum += interest
            MonthPayback(i + 1, Mathematics.round(principalPayback + interest, 2), principalPayback, interest)
        })
        val totalAmount = Mathematics.round(principal + interestSum, 2)
        val totalInterest = Mathematics.round(interestSum, 2)
        PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details)
    }

    /**
     * 等额本息还款详情
     *
     * @param principal 本金
     * @param rate      月利率（此时只是名义利率）
     * @param periods   还款期数
     * @return
     */
    def averageCapitalPlusInterest(principal: Double, rate: Double, periods: Int): PaybackDetails = {
        val totalInterest = Mathematics.round(principal * rate * periods, 2)
        val totalAmount = principal + totalInterest
        val principalPayback = Mathematics.round(principal / periods, 2)
        val interest = Mathematics.round(principal * rate, 2)
        val paybackPerMonth = principalPayback + interest
        val details = (0 until periods).map { i => MonthPayback(i + 1, paybackPerMonth, principalPayback, interest) }
        PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details)
    }

    /**
     * 等额本息反推实际月利率
     *
     * @param principal    本金
     * @param monthPayback 月还款额
     * @param periods      还款期数
     * @return
     */
    def rate(principal: Double, monthPayback: Double, periods: Int): Double = {
        var guess = 0.1
        var payback = this.averageCapitalPlusInterestOfHouseLoan(principal, guess, periods).totalPayback / periods
        while (Math.abs(monthPayback - payback) > 0.0001) {
            if (monthPayback > payback)
                guess += guess / 2.0
            else
                guess /= 2.0
            payback = this.averageCapitalPlusInterestOfHouseLoan(principal, guess, periods).totalPayback / periods
        }
        Mathematics.round(guess, 4)
    }

    /**
     * 房贷等额本息还款详情
     * 设贷款总额为A，月利率为β，总还款月数为m，则月还款额X=\frac{Aβ(1+β)^m}{(1+β)^m-1}
     *
     * @param principal 本金
     * @param rate      月利率（这里才是实际利率）
     * @param periods   还款期数
     * @return
     */
    def averageCapitalPlusInterestOfHouseLoan(principal: Double, rate: Double, periods: Int): PaybackDetails = {
        val paybackPerMonth = Mathematics.round(principal * rate * Math.pow(rate + 1, periods) / (Math.pow(rate + 1, periods) - 1), 2)
        var restAmount = principal
        val details = (0 until periods).map(i => {
            val interest = Mathematics.round(restAmount * rate, 2)
            val principalPayback = Mathematics.round(paybackPerMonth - interest, 2)
            restAmount -= principalPayback
            MonthPayback(i + 1, paybackPerMonth, principalPayback, interest)
        })
        val totalAmount = paybackPerMonth * periods
        val totalInterest = paybackPerMonth * periods - principal
        PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details)
    }

    /**
     * 月还款详情
     *
     * @param period    第几期
     * @param payback   还款额
     * @param principle 本金
     * @param interest  利息
     */
    case class MonthPayback(period: Int, payback: Double, principle: Double, interest: Double)

    /**
     * 贷款还款详情
     *
     * @param principle           本金
     * @param rate                月利率
     * @param periods             期数
     * @param totalPayback        总还款额
     * @param totalInterest       总利息
     * @param monthPaybackDetails 每月还款详情
     */
    case class PaybackDetails(principle: Double, rate: Double, periods: Int, totalPayback: Double, totalInterest: Double, monthPaybackDetails: Seq[MonthPayback])

    implicit class PaybackDisplay(paybackDetails: PaybackDetails) {

        def display(render: String = "0;32"): Unit = {
            s"贷款${paybackDetails.principle}元，月利率${paybackDetails.rate}".prettyPrintln(render)
            s"总还款额${paybackDetails.totalPayback}元，总利息${paybackDetails.totalInterest}元".prettyPrintln(render)
            "还款详情如下：".prettyPrintln(render)
            paybackDetails.monthPaybackDetails
                .map { e => List(e.period.toString, e.payback.toString, e.principle.toString, e.interest.toString) }
                .prettyShow(render, -1, explode = false, 0, 0, flank = true, null2Empty = true, transverse = true, truncate = false, 0, vertical = true, columns = List("Period", "Month Payback", "Principle", "Interest"))
        }

    }

}
