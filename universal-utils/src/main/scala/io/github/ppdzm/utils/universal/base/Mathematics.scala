package io.github.ppdzm.utils.universal.base

import scala.util.Random


/**
 * Created by Stuart Alex on 2017/5/24.
 */
object Mathematics {

    /**
     * 求百分位数
     *
     * @param data 一组数据
     * @param p    百分位
     * @return
     */
    def percentile(data: Array[Int], p: Double): Double = {
        percentile(data.map(_.toDouble), p)
    }

    /**
     * 求百分位数
     *
     * @param data 一组数据
     * @param p    百分位
     * @return
     */
    def percentile(data: Array[Double], p: Double): Double = {
        val sorted = data.sortWith((a, b) => a.compare(b) <= 0)
        val number = data.length
        val k = p * (number - 1)
        val i = k.toInt
        sorted(i) + (k - i) * (sorted(i + 1) - sorted(i))
    }

    /**
     * 随机整数
     *
     * @return
     */
    def randomInt(): Int = Random.nextInt()

    /**
     * 有上限的随机整数
     *
     * @param ceiling 随机整数上限值
     * @return
     */
    def randomInt(ceiling: Int): Int = Random.nextInt(ceiling)

    /**
     * 有上限和下限的随机整数
     *
     * @param floor   随机整数下限值
     * @param ceiling 随机整数上限值
     * @return
     */
    def randomInt(floor: Int, ceiling: Int): Int = {
        if (floor > ceiling)
            throw new IllegalArgumentException("Floor can not large than ceiling")
        if (floor == ceiling)
            floor
        else
            floor + Random.nextInt(ceiling - floor + 1)
    }

    /**
     * 按指定精度取不大于某个数的最大数
     *
     * @param numeric   数
     * @param precision 精度
     * @return
     */
    def floor(numeric: Double, precision: Int): Double = {
        val times = Math.pow(10, precision)
        val afterTimes = numeric * times
        if (Math.round(afterTimes) > afterTimes)
            (Math.round(afterTimes) - 1) / times
        else
            Math.round(afterTimes) / times
    }

    /**
     * 按指定精度取不小于某个数的最小数
     *
     * @param numeric   数
     * @param precision 精度
     * @return
     */
    def ceiling(numeric: Double, precision: Int): Double = {
        val times = Math.pow(10, precision)
        val afterTimes = numeric * times
        if (Math.round(afterTimes) < afterTimes)
            (Math.round(afterTimes) + 1) / times
        else
            Math.round(afterTimes) / times
    }

    /**
     * 按指定精度四舍五入
     *
     * @param numeric   数
     * @param precision 精度
     * @return
     */
    def round(numeric: Double, precision: Int): Double = {
        val times = Math.pow(10, precision)
        val afterTimes = numeric * times
        Math.round(afterTimes) / times
    }

    /**
     * 连续自然数1~n的和
     *
     * @param n 自然数
     * @return
     */
    def powerSum1(n: Int): Int = n * (n + 1) / 2

    /**
     * 连续自然数1~n的平方和
     *
     * @param n 自然数
     * @return
     */
    def powerSum2(n: Int): Int = n * (n + 1) * (2 * n + 1) / 6

    /**
     * 连续自然数1~n的立方和
     *
     * @param n 自然数
     * @return
     */
    def powerSum3(n: Int): Int = n * n * (n + 1) * (n + 1) / 4

    /**
     * 连续自然数1~n的四次方和
     *
     * @param n 自然数
     * @return
     */
    def powerSum4(n: Int): Int = n * (n + 1) * (6 * n * n * n + 9 * n * n + n - 1) / 30

    /**
     * 连续自然数1~n的k次方求和
     *  note.youdao.com/share/?id=83d59f16088ca0a0e0b6bbd2ad497785&type=note#/
     *
     * @param n 自然数
     * @param k 次方
     * @return
     */
    def powerSum(n: Int, k: Int): Int = (1 to n).map(Math.pow(_, k)).sum.toInt

}