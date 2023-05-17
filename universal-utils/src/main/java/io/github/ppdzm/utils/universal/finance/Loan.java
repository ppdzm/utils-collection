package io.github.ppdzm.utils.universal.finance;

import io.github.ppdzm.utils.universal.base.Mathematics;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2017/9/22.
 */
public class Loan {

    /**
     * 等额本金还款详情
     *
     * @param principal 本金
     * @param rate      月利率
     * @param periods   还款期数
     * @return PaybackDetails
     */
    public static PaybackDetails averageCapital(double principal, double rate, int periods) {
        double restAmount = principal;
        double interestSum = 0.0;
        List<MonthPayback> details = new ArrayList<>();
        for (int i = 0; i < periods; i++) {
            double principalPayback = Mathematics.round(principal / periods, 2);
            double interest = Mathematics.round(restAmount * rate, 2);
            restAmount -= principalPayback;
            interestSum += interest;
            details.add(new MonthPayback(i + 1, Mathematics.round(principalPayback + interest, 2), principalPayback, interest));
        }
        double totalAmount = Mathematics.round(principal + interestSum, 2);
        double totalInterest = Mathematics.round(interestSum, 2);
        return new PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details);
    }

    /**
     * 等额本息还款详情
     *
     * @param principal 本金
     * @param rate      月利率（此时只是名义利率）
     * @param periods   还款期数
     * @return PaybackDetails
     */
    public PaybackDetails averageCapitalPlusInterest(double principal, double rate, int periods) {
        double totalInterest = Mathematics.round(principal * rate * periods, 2);
        double totalAmount = principal + totalInterest;
        double principalPayback = Mathematics.round(principal / periods, 2);
        double interest = Mathematics.round(principal * rate, 2);
        double paybackPerMonth = principalPayback + interest;
        List<MonthPayback> details = new ArrayList<>();

        for (int i = 0; i < periods; i++) {
            details.add(new MonthPayback(i + 1, paybackPerMonth, principalPayback, interest));
        }

        return new PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details);
    }

    /**
     * 等额本息反推实际月利率
     *
     * @param principal    本金
     * @param monthPayback 月还款额
     * @param periods      还款期数
     * @return double
     */
    public double rate(double principal, double monthPayback, int periods) {
        double guess = 0.1;
        double payback = averageCapitalPlusInterestOfHouseLoan(principal, guess, periods).getTotalPayback() / periods;

        while (Math.abs(monthPayback - payback) > 0.0001) {
            if (monthPayback > payback) {
                guess += guess / 2.0;
            } else {
                guess /= 2.0;
            }

            payback = averageCapitalPlusInterestOfHouseLoan(principal, guess, periods).getTotalPayback() / periods;
        }

        return Mathematics.round(guess, 4);
    }

    /**
     * 房贷等额本息还款详情
     * 设贷款总额为A，月利率为β，总还款月数为m，则月还款额X=\frac{Aβ(1+β)^m}{(1+β)^m-1}
     *
     * @param principal 本金
     * @param rate      月利率（这里才是实际利率）
     * @param periods   还款期数
     * @return PaybackDetails
     */
    public PaybackDetails averageCapitalPlusInterestOfHouseLoan(double principal, double rate, int periods) {
        double paybackPerMonth = Mathematics.round(principal * rate * Math.pow(rate + 1, periods) / (Math.pow(rate + 1, periods) - 1), 2);
        double restAmount = principal;
        List<MonthPayback> details = new ArrayList<>();

        for (int i = 0; i < periods; i++) {
            double interest = Mathematics.round(restAmount * rate, 2);
            double principalPayback = Mathematics.round(paybackPerMonth - interest, 2);
            restAmount -= principalPayback;
            details.add(new MonthPayback(i + 1, paybackPerMonth, principalPayback, interest));
        }

        double totalAmount = paybackPerMonth * periods;
        double totalInterest = paybackPerMonth * periods - principal;

        return new PaybackDetails(principal, rate, periods, totalAmount, totalInterest, details);
    }

    @AllArgsConstructor
    @Data
    public static class MonthPayback {
        public int period;
        public double payback;
        public double principle;
        public double interest;
    }

    @AllArgsConstructor
    @Data
    public static class PaybackDetails {
        public double principle;
        public double rate;
        public int periods;
        public double totalPayback;
        public double totalInterest;
        public List<MonthPayback> monthPaybackDetails;
    }

}
