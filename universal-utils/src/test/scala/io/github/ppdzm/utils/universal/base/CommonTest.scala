package io.github.ppdzm.utils.universal.base

import io.github.ppdzm.utils.universal.implicits.ArrayConversions._
import org.apache.commons.io._
import org.scalatest._

/**
 * Created by Stuart Alex on 2021/4/8.
 */
class CommonTest extends FunSuite with Logging {
    test("filename-utils") {
        println(FilenameUtils.removeExtension("a.txt"))
        println(FilenameUtils.getExtension("a.txt"))
    }

    test("combination") {
        Array(1, 2, 3, 4).getCombination(0).foreach(println)
        println()
        Array(1, 2, 3, 4).getCombination(1).foreach(println)
        println()
        Array(1, 2, 3, 4).getCombination(2).foreach(println)
        println()
        Array(1, 2, 3, 4).getCombination(3).foreach(println)
        println()
        Array(1, 2, 3, 4).getCombination(4).foreach(println)
        println()
        Array(1, 2, 3, 4).getCombination().foreach(println)
    }

}
