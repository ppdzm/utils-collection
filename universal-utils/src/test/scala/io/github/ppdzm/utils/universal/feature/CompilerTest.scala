package io.github.ppdzm.utils.universal.feature

import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/3/20.
 */
class CompilerTest extends FunSuite {

    test("compile") {
        println(Compiler.evaluate[Boolean]("1<2"))
        println(Compiler.evaluate[Int]("1+2+3+4+5"))
        println(Compiler.evaluate[String](""""hello" + " " + "scala :)""""))
    }

    test("exception generator") {
        val x = ExceptionGenerator.newException("HaHa", "ha ha ha ha")
        x.printStackTrace()
        println(x.getClass)
        println(x)
    }

}