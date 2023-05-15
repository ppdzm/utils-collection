package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.base.Symbols._
import io.github.ppdzm.utils.universal.base.{Mathematics, StringUtils}
import io.github.ppdzm.utils.universal.cli.{Alignment, CliUtils, Render}
import io.github.ppdzm.utils.universal.implicits.UnitConversions._

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object BasicConversions {

    implicit class AnyImplicits(any: Any) {

        /**
         * 不是null
         *
         * @return
         */
        def notNull: Boolean = any != null

        /**
         * 如果是null，返回空字符串
         *
         * @return
         */
        def n2e: String = if (any.isNull) "" else any.toString

        /**
         * 是null
         *
         * @return
         */
        def isNull: Boolean = any == null

        /**
         * 如果是null，且允许转换，则返回空字符串
         *
         * @param null2Empty 是否进行null到空字符串的转换
         * @return
         */
        def n2e(null2Empty: Boolean): String = {
            if (any.isNull) {
                if (null2Empty)
                    ""
                else
                    "NULL"
            } else {
                any.toString
            }
        }

        def prettyPrintln(renders: Render*): Unit = any.toString.prettyPrintln(renders: _*)

    }

    implicit class CharImplicits(char: Char) {

        /**
         * 是否是符号
         *
         * @return
         */
        def isSymbol: Boolean = (char >= 32 && char <= 47) || (char >= 58 && char <= 64) || (char >= 91 && char <= 96) || (char >= 123 && char <= 127)

        /**
         * 是否是中日韩文字或符号
         *
         * @return
         */
        def isCJK: Boolean = {
            val ub = Character.UnicodeBlock.of(char)
            ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
              ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
              ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
              ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
              ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
              ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
              ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
        }

    }

    implicit class NumberImplicits(double: Double) {

        def percent(precision: Int): String = Mathematics.round(double * 100, precision).toString.trimEnd("0").trimEnd(".") + "%"

    }

    implicit class StringImplicits(string: String) {
        private lazy val specialSymbol = Array(lineSeparator, tab, backQuote, "\1")

        /**
         * 在字符串两侧加上双引号
         *
         * @return
         */
        def quote = s""""$string""""

        /**
         * 去除字符串两侧成对出现的的引号
         *
         * @param recursive 是否递归去除
         * @return
         */
        def dequote(recursive: Boolean): String = {
            if (recursive) {
                if ((string.startsWith(singleQuote) && string.endsWith(singleQuote)) || (string.startsWith(doubleQuote) && string.endsWith(doubleQuote)))
                    return string.substring(1, string.length - 1).dequote(recursive)
            } else {
                if ((string.startsWith(singleQuote) && string.endsWith(singleQuote)) || (string.startsWith(doubleQuote) && string.endsWith(doubleQuote)))
                    return string.substring(1, string.length - 1)
            }
            string
        }

        /**
         * Unicode转文本
         *
         * @return
         */
        def deUnicode: String = {
            string
              .split("""\\u""")
              .filter(_.nonEmpty)
              .map {
                  e =>
                      if (e.length < 4)
                          e
                      else {
                          val left = e.substring(0, 4)
                          val right = e.substring(4)
                          val r = Try(Integer.parseInt(left, 16).toChar)
                          if (r.isSuccess)
                              r.get + right
                          else
                              e
                      }
              }
              .mkString
        }

        /**
         * \\x形式Unicode文本加转义
         *
         * @return
         */
        def escapeUnicode8: String = {
            val unicodeString = string
            val stringBuilder = new StringBuilder()
            unicodeString
              .indices
              .foreach {
                  i =>
                      if (unicodeString(i) == '\\' && i < unicodeString.length && unicodeString(i + 1) == 'x')
                          stringBuilder.append("""\\""")
                      else
                          stringBuilder.append(unicodeString(i))
              }
            stringBuilder.mkString
        }

        /**
         * \x形式Unicode文本去转义
         *
         * @return
         */
        def unescapeUnicode8: String = {
            val unicodeEscapedString = string
            unicodeEscapedString.replace("""\\x""", """\x""")
        }

        /**
         * 判断字符串是否非空
         *
         * @return
         */
        def notNullAndEmpty: Boolean = !isNullOrEmpty

        /**
         * 去除注释
         *
         * @return
         */
        def trimComment: String = {
            var singleQuoteFlag = false
            var dashFlag = false
            string.indices.foreach(i => {
                if (string(i).toString == singleQuote) {
                    if (singleQuoteFlag)
                        dashFlag = false
                    singleQuoteFlag = !singleQuoteFlag
                } else if (string(i).toString == "-") {
                    if (dashFlag && !singleQuoteFlag) {
                        return string.substring(0, i - 1).trim
                    }
                    dashFlag = true
                } else {
                    dashFlag = false
                }
            })
            string
        }

        /**
         * \x形式Unicode文本转字节
         *
         * @return
         */
        def unicode82Bytes: Array[Byte] = {
            val unicodeText = string
            unicodeText
              .split("""\\x""")
              .flatMap {
                  e =>
                      if (e.length == 2)
                          e.hexString2Bytes
                      else {
                          e.take(2).hexString2Bytes ++ e.drop(2).flatMap(_.toString.getBytes())
                      }
              }
        }

        /**
         * 是否为注释
         *
         * @return
         */
        def isComment: Boolean = string.startsWith("#") || string.startsWith("--")

        /**
         * 是否是可读字符串
         *
         * @return
         */
        def isUnderstandable: Boolean = !string.exists(c => !c.isCJK && !c.isDigit && !c.isLetter && !c.isSymbol)

        /**
         * 计算字符串在显示器上的宽度，CJK字符按长度2计算
         *
         * @return
         */
        def width: Int = {
            string.map(char => {
                if (char.toString == lineFeed)
                    1
                else if (char.isCJK)
                    2
                else
                    1
            }).sum
        }

        /**
         * 按宽度切片
         *
         * @param width 切片宽度
         */
        def sliceByWidth(width: Int): Array[String] = {
            val slices = ArrayBuffer[String]()
            var buffer = ""
            string.foreach(char => {
                if (buffer.width + char.toString.width > width) {
                    slices += buffer
                    buffer = ""
                }
                buffer += char.toString
            })
            if (buffer != "")
                slices += buffer
            slices.toArray
        }

        /**
         * 按长度切片
         *
         * @param size 切片长度
         */
        def sliceByLength(size: Int): Iterator[String] = string.sliding(size, size)

        /**
         * 填充指定字符到指定从长度
         *
         * @param alignment 原字符串在结果中的对齐位置
         * @param length    填充后的长度
         * @param char      被填充的字符
         * @return
         */
        def pad(length: Int, char: Char, alignment: Any): String = {
            val paddingNumber = length - string.length
            if (paddingNumber >= 0) {
                Alignment.construct(alignment) match {
                    case Alignment.LEFT => string + List.fill(paddingNumber)(char).mkString
                    case Alignment.CENTER => List.fill(paddingNumber / 2)(char).mkString + string + List.fill(paddingNumber / 2 + paddingNumber % 2)(char).mkString
                    case Alignment.RIGHT => List.fill(paddingNumber)(char).mkString + string
                }
            } else
                string
        }

        /**
         * 当满足条件时替换
         *
         * @param condition 条件
         * @param oldChar   被替换的子串
         * @param newChar   替换后的子串
         * @return
         */
        def replaceIf(condition: => Boolean, oldChar: CharSequence, newChar: CharSequence): String = {
            if (condition)
                return string.replace(oldChar, newChar)
            string
        }

        /**
         * 去除特殊字符
         *
         * @return
         */
        def replaceSpecialSymbol: String = {
            var temp = string
            this.specialSymbol.foreach(s => temp = temp.replace(s, ""))
            temp.trim
        }

        /**
         * 循环替换
         *
         * @param oldChar 被替换的子串
         * @param newChar 替换后的子串
         * @return
         */
        def recursiveReplace(oldChar: CharSequence, newChar: CharSequence): String = {
            if (string.contains(oldChar))
                string.replace(oldChar, newChar).recursiveReplace(oldChar, newChar)
            else
                string
        }

        /**
         * 去除字符串头部的特定字符串
         *
         * @param s 要去除的字符串
         * @return
         */
        def trimStart(s: String): String = {
            if (string.startsWith(s))
                string.drop(s.length).trimStart(s)
            else
                string
        }

        /**
         * 去除字符串尾部的特定字符串
         *
         * @param s 要去除的字符串
         * @return
         */
        def trimEnd(s: String): String = {
            if (string.endsWith(s))
                string.dropRight(s.length).trimStart(s)
            else
                string
        }

        def trim(s: String): String = this.trimStart(s).trimEnd(s).trim

        def cyan: String = CliUtils.rendering(string, Render.RESET, Render.CYAN)

        def green: String = CliUtils.rendering(string, Render.RESET, Render.GREEN)

        def magenta: String = CliUtils.rendering(string, Render.RESET, Render.MAGENTA)

        def red: String = CliUtils.rendering(string, Render.RESET, Render.RED)

        def yellow: String = CliUtils.rendering(string, Render.RESET, Render.YELLOW)

        def boldGreen: String = CliUtils.rendering(string, Render.RESET, Render.BOLD, Render.GREEN_B)

        def boldRed: String = CliUtils.rendering(string, Render.RESET, Render.BOLD, Render.RED_B)

        /**
         * 把MySQL字段的数据类型转换为Hive字段的数据类型
         *
         * @return
         */
        def toHiveDataType: String = {
            string.toLowerCase.replace("type", "") match {
                case "integer" => "int"
                case "long" => "bigint"
                case other => other
            }
        }

        /**
         * 对文本进行颜色渲染打印
         *
         * @param renders 渲染参数
         * @return
         */
        def prettyPrint(renders: Render*): Unit = print(string.rendering(renders: _*))

        /**
         * 对文本进行颜色渲染打印
         *
         * @param renders 渲染参数
         * @return
         */
        def prettyPrint(renders: String): Unit = print(string.rendering(renders))

        /**
         * 对文本进行颜色渲染打印后换行
         *
         * @param renders 渲染参数
         * @return
         */
        def prettyPrintln(renders: Render*): Unit = println(string.rendering(renders: _*))

        /**
         * 对文本进行颜色渲染打印后换行
         *
         * @param renders 渲染参数
         * @return
         */
        def prettyPrintln(renders: String): Unit = println(string.rendering(renders))

        /**
         * 对文本进行颜色渲染
         *
         * @param renders 渲染参数
         * @return
         */
        def rendering(renders: Render*): String = {
            if (renders == null || renders.isEmpty)
                string
            else
                CliUtils.rendering(string, renders: _*)
        }

        /**
         * 对文本进行颜色渲染
         *
         * @param renders 渲染参数
         * @return
         */
        def rendering(renders: String): String = {
            if (renders == null || renders.isEmpty)
                string
            else
                CliUtils.rendering(string, renders.split(";").map(_.toInt).map(Render.valueOf): _*)
        }

        /**
         * 字符串是null或空字符串
         *
         * @return
         */
        def isNullOrEmpty: Boolean = string.isNull || string == ""

        def splitDoubleQuotedString(splitter: String): Array[String] = StringUtils.split(string, splitter)

        def ipV4Address2Int: Int = {
            string.split("\\.")
              .zipWithIndex
              .map { case (value, index) => value.toInt << ((3 - index) * 8) }
              .sum
        }

        def toCamel: String = {
            var index = 0
            var newValue = ""
            while (index < string.length) {
                if (string(index) == '_') {
                    if (index + 1 < string.length)
                        newValue += string(index + 1).toUpper
                    index += 2
                } else {
                    newValue += string(index)
                    index += 1
                }
            }
            newValue
        }

        def toPascal: String = {
            var index = 0
            var newValue = ""
            while (index < string.length) {
                if (string(index).isUpper) {
                    newValue += "_" + string(index).toLower
                } else {
                    newValue += string(index)
                }
                index += 1
            }
            newValue
        }

    }

}
