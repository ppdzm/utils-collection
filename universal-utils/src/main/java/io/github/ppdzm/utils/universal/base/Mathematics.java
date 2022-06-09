package io.github.ppdzm.utils.universal.base;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Created by Stuart Alex on 2021/6/19.
 */
public class Mathematics {
    /**
     * 求百分位数
     *
     * @param data 一组数据
     * @param p    百分位
     * @return double
     */
    public static double percentile(int[] data, double p) {
        Arrays.sort(data);
        int number = data.length;
        double k = p * (number - 1);
        int i = (int) k;
        return data[i] + (k - i) * (data[i + 1] - data[i]);
    }

    /**
     * 求百分位数
     *
     * @param data 一组数据
     * @param p    百分位
     * @return double
     */
    public static double percentile(double[] data, double p) {
        Arrays.sort(data);
        int number = data.length;
        double k = p * (number - 1);
        int i = (int) k;
        return data[i] + (k - i) * (data[i + 1] - data[i]);
    }

    /**
     * 随机整数
     *
     * @return int
     */
    public static int randomInt() {
        return new Random().nextInt();
    }

    /**
     * 有上限的随机整数
     *
     * @param ceiling 随机整数上限值
     * @return int
     */
    public static int randomInt(int ceiling) {
        return new Random().nextInt(ceiling);
    }

    /**
     * 有上限和下限的随机整数
     *
     * @param floor   随机整数下限值
     * @param ceiling 随机整数上限值
     * @return int
     */
    public static int randomInt(int floor, int ceiling) {
        if (floor > ceiling) {
            throw new IllegalArgumentException("Floor can not large than ceiling");
        }
        if (floor == ceiling) {
            return floor;
        } else {
            return floor + randomInt(ceiling - floor + 1);
        }
    }

    /**
     * 按指定精度取不大于某个数的最大数
     *
     * @param numeric   数值
     * @param precision 精度
     * @return double
     */
    public static double floor(double numeric, int precision) {
        double times = Math.pow(10, precision);
        double afterTimes = numeric * times;
        if (Math.round(afterTimes) > afterTimes) {
            return (Math.round(afterTimes) - 1) / times;
        } else {
            return Math.round(afterTimes) / times;
        }
    }

    /**
     * 按指定精度取不小于某个数的最小数
     *
     * @param numeric   数值
     * @param precision 精度
     * @return double
     */
    public static double ceiling(double numeric, int precision) {
        double times = Math.pow(10, precision);
        double afterTimes = numeric * times;
        if (Math.round(afterTimes) < afterTimes) {
            return (Math.round(afterTimes) + 1) / times;
        } else {
            return Math.round(afterTimes) / times;
        }
    }

    /**
     * 按指定精度四舍五入
     *
     * @param numeric   数值
     * @param precision 精度
     * @return double
     */
    public static double round(double numeric, int precision) {
        double times = Math.pow(10, precision);
        double afterTimes = numeric * times;
        return Math.round(afterTimes) / times;
    }

    /**
     * 连续自然数1~n的和
     *
     * @param n 自然数
     * @return int
     */
    public static int powerSum1(int n) {
        return n * (n + 1) / 2;
    }

    /**
     * 连续自然数1~n的平方和
     *
     * @param n 自然数
     * @return int
     */
    public static int powerSum2(int n) {
        return n * (n + 1) * (2 * n + 1) / 6;
    }

    /**
     * 连续自然数1~n的立方和
     *
     * @param n 自然数
     * @return int
     */
    public static int powerSum3(int n) {
        return n * n * (n + 1) * (n + 1) / 4;
    }

    /**
     * 连续自然数1~n的四次方和
     *
     * @param n 自然数
     * @return int
     */
    public static int powerSum4(int n) {
        return n * (n + 1) * (6 * n * n * n + 9 * n * n + n - 1) / 30;
    }

    /**
     * 连续自然数1~n的k次方求和
     * note.youdao.com/share/?id=83d59f16088ca0a0e0b6bbd2ad497785&type=note#/
     *
     * @param n 自然数
     * @param k 次方
     * @return int
     */
    public static int powerSum(int n, int k) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += Math.pow(i, k);
        }
        return sum;
    }
}
