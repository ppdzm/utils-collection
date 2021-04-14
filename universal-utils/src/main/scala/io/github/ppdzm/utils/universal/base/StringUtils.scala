package io.github.ppdzm.utils.universal.base

import io.github.ppdzm.utils.universal.implicits.BasicConversions._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
 * Created by Stuart Alex on 2017/1/3.
 */
object StringUtils {

    /**
     * 返回一个长度为length的随机字符串，字符串仅包含26个英文字母的大写和小写以及阿拉伯数字0-9
     *
     * @param length 随机字符串长度
     * @return String
     */
    def randomString(length: Int, chars: Array[Char] = ((48 to 57) union (65 to 90) union (97 to 122)).map(_.toChar).toArray): String = {
        Array.fill(length)(Random.nextInt(chars.length)).map(chars(_)).mkString
    }

    /**
     * 分隔字符串，保留成对双引号包裹的子串
     *
     * @param string   原始字符串
     * @param splitter 分隔符
     * @return
     */
    def split(string: String, splitter: String): Array[String] = {
        val splits = string.split(splitter).map(_.trim)
        val expectedSplits = ArrayBuffer[String]()
        var buffer = ""
        splits.indices.foreach(i => {
            splits(i) match {
                case _ if splits(i).startsWith("\"") && splits(i).endsWith("\"") =>
                    expectedSplits += splits(i)
                case _ if splits(i).startsWith("\"") =>
                    if (buffer != "")
                        buffer += splitter + splits(i)
                    else
                        buffer = splits(i)
                case _ if splits(i).endsWith("\"") =>
                    if (buffer != "") {
                        expectedSplits += buffer + splitter + splits(i)
                        buffer = ""
                    }
                    else
                        expectedSplits += splits(i)
                case _ =>
                    if (buffer != "")
                        buffer += splitter + splits(i)
                    else
                        expectedSplits += splits(i)
            }
        })
        if (buffer != "")
            expectedSplits += buffer
        expectedSplits.map(_.dequote(false)).toArray
    }

}