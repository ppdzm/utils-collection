package io.github.ppdzm.utils.universal.cli

import io.github.ppdzm.utils.universal.base.Enum

import scala.util.Try

/**
 * Created by Stuart Alex on 2017/8/24.
 */
object Alignment extends Enum {
    val left: Alignment.Value = Value(-1, "left")
    val center: Alignment.Value = Value(0, "center")
    val right: Alignment.Value = Value(1, "right")

    def construct(any: Any): Alignment.Value = {
        any match {
            case _: Int => Alignment(any.toString.toInt)
            case _: String => this.construct(any.toString)
            case _: Alignment.Value => any.asInstanceOf[Alignment.Value]
            case _ => throw new IllegalArgumentException(s"Constructor parameter $any is illegal for Alignment")
        }
    }

    private def construct(s: String): Alignment.Value = {
        if (Try(s.toInt).isSuccess)
            Alignment.values.find(_.id == s.toInt).getOrElse(throw new IllegalArgumentException(s"ID $s is illegal for Alignment"))
        else
            this.values.find(_.toString == s).getOrElse(throw new IllegalArgumentException(s"Name $s is illegal for Alignment"))
    }

}
