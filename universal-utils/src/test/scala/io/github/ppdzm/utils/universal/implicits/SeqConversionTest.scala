package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.sa.utils.universal.config.Config
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2021/4/6.
 */
class SeqConversionTest extends FunSuite {
    implicit protected val config: Config = FileConfig()

    test("console-table") {
        val configuredConversions = new ConfiguredConversions()
        import configuredConversions._
        val columns = List("Number", "Name", "Age", "extra")
        val data = List(
            List(1, "Alex", 30, "It was not exactly smooth sailing for Woods, but in the end his closing three-under-par 67 was enough to hold off a challenge from Hideki Matsuyama at Narashino Country Club in the first PGA Tour event played in Japan."),
            List(2, "Bob", 45, "Woods, who had to play seven holes on Monday in the weather-affected event, finished at 19-under 261 in his first tournament since undergoing arthroscopic left knee surgery two months ago.")
        )
        // 000
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 010
        PRINT_BORDER_TRANSVERSE.newValue(true)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 011
        PRINT_BORDER_FLANK.newValue(true)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 111
        PRINT_BORDER_VERTICAL.newValue(true)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 110
        PRINT_BORDER_FLANK.newValue(false)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 100
        PRINT_BORDER_TRANSVERSE.newValue(false)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 101
        PRINT_BORDER_FLANK.newValue(false)
        data.prettyShow(columns = columns)
        println("~" * 100)
        // 001
        PRINT_BORDER_VERTICAL.newValue(false)
        data.prettyShow(columns = columns)
        println("~" * 100)
    }

    test("vertical") {
        import SeqConversions._
        val data = List(List(-1, "open", "Hive"), List(-2, "open", "HBase"))
        val vertical = data.toVertical(columns = List("序号", "状态", "地址"))
        vertical.foreach(println)
    }
}
