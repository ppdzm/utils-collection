package io.github.ppdzm.utils.hadoop.kafka.functions

/**
 * Created by Stuart Alex on 2021/1/29.
 */
trait Condition {

    def isTrue: Boolean

}

object AlwaysTrueCondition extends Condition {
    override def isTrue: Boolean = true
}

case class CountCondition(max: Int) extends Condition {
    private var counter: Int = 0

    override def isTrue: Boolean = {
        counter += 1
        counter <= max
    }
}

case class TimeCondition(timeInSeconds: Int) extends Condition {
    private val atFirst = System.currentTimeMillis()

    override def isTrue: Boolean = {
        System.currentTimeMillis() - atFirst < timeInSeconds * 1000
    }
}