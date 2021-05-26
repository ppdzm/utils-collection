package io.github.ppdzm.utils.universal.insurance

import io.github.ppdzm.utils.universal.base.Mathematics

/**
 * Created by Stuart Alex on 2021/2/23.
 */
object ExperienceLiveTable {
    def fromDeadList(deadList: List[Double]): Map[Int, TableRow] = {
        val maxAge: Int = deadList.length - 1
        val temps = deadList.indices.map {
            age =>
                val survivalAtStart = deadList.drop(age).sum
                val deadAtEnd = deadList(age)
                val deadRate = Mathematics.round(deadAtEnd / survivalAtStart, 6)
                age -> TableRow(age, survivalAtStart, deadAtEnd, 1 - deadRate, deadRate, -1)
        }
        temps.map {
            case (age, r) =>
                if (age == maxAge)
                    age -> TableRow(r.age, r.l, r.d, r.p, r.q, 0.5)
                else
                    age -> TableRow(r.age, r.l, r.d, r.p, r.q, Mathematics.round(temps.filter(_._1 >= age).map(_._2).map(_.l).sum / r.l + 0.5, 1))
        }.toMap
    }
}

trait ExperienceLiveTable {
    private lazy val maxAge: Int = rows.size - 1
    protected val rows: Map[Int, TableRow]
    protected val v: Double

    def a(x: Int, n: Int): Double = (m(x) - m(x + n)) / d(x)

    def p(x: Int, n: Int): Double = 1 - q(x, n)

    def q(x: Int, n: Int): Double = Mathematics.round(rows.slice(x, x + n).values.map(_.d).sum * 1.0 / rows(x).l, 6)

    def q(x: Int, n: Int, u: Int): Double = Mathematics.round(rows.slice(x + n, x + n + u).values.map(_.d).sum * 1.0 / rows(x).l, 6)

    def r(x: Int): Double = (x to maxAge).map(m).sum

    def m(x: Int): Double = (x to maxAge).map(c).sum

    def c(x: Int): Double = Math.pow(v, x + 1) * rows(x).d

    def s(x: Int): Double = (x to maxAge).map(n).sum

    def n(x: Int): Double = (x to maxAge).map(d).sum

    def d(x: Int): Double = Math.pow(v, x) * rows(x).l

}

case class TableRow(age: Int, l: Double, d: Double, p: Double, q: Double, e: Double)