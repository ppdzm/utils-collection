package io.github.ppdzm.utils.universal.insurance

/**
 * Created by Stuart Alex on 2021/2/23.
 */
class FemalePensionExperienceLiveTable2000_2003(i: Double) extends ExperienceLiveTable {
    override protected val v: Double = 1 / (1 + i)
    override protected val rows: Map[Int, TableRow] = ExperienceLiveTable.fromDeadList(deadList)
    //TODO
    private val deadList: List[Double] = List()
}
