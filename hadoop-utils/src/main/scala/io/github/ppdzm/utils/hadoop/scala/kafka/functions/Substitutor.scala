package io.github.ppdzm.utils.hadoop.scala.kafka.functions

/**
 * Created by Stuart Alex on 2021/2/25.
 */
trait Substitutor {
    val placeholder: String

    def getSubstitutor: String

}

object NoSubstitutor extends Substitutor {
    override val placeholder: String = null

    override def getSubstitutor: String = null
}

case class NumberSubstitutor(override val placeholder: String) extends Substitutor {
    private var number = 0

    override def getSubstitutor: String = {
        number += 1
        number.toString
    }
}