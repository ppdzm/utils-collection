package io.github.ppdzm.utils.universal.base

import org.scalatest.FunSuite

class DateTimeUtilsTest extends FunSuite {

    test("format") {
        println(DateTimeUtils.nowWithFormat("yyyy-MM-dd HH:mm:ss.SSS"))
        println(DateTimeUtils.format(DateTimeUtils.unixTime2Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss.SSS"))
        println(DateTimeUtils.format(DateTimeUtils.unixTime2Date(System.currentTimeMillis() * 10), "yyyy-MM-dd HH:mm:ss.SSS"))
        println(DateTimeUtils.format(DateTimeUtils.unixTime2Date(System.currentTimeMillis() * 100), "yyyy-MM-dd HH:mm:ss.SSS"))
        println(DateTimeUtils.format(DateTimeUtils.unixTime2Date(System.currentTimeMillis() * 1000), "yyyy-MM-dd HH:mm:ss.SSS"))
    }

}
