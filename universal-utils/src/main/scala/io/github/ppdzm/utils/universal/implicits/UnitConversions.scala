package io.github.ppdzm.utils.universal.implicits

import java.nio.charset.StandardCharsets

import io.github.ppdzm.utils.universal.base.{BytesUtils, Mathematics}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

import scala.collection.mutable

/**
 * Created by Stuart Alex on 2017/9/4.
 */
object UnitConversions {

    implicit class BytesArrayImplicits(bytes: Array[Byte]) {

        /**
         * 字节数组转换为文本
         *
         * @return
         */
        def toRealString: String = {
            if (bytes == null)
                return null
            new String(bytes, StandardCharsets.UTF_8.name())
        }

        /**
         * 字节数组转换为以0x开头的十六进制字符串
         *
         * @return
         */
        def toHexStringWith0X: String = "0x" + this.toHexString

        /**
         * 字符串转二进制字符串
         *
         * @return
         */
        def toBinaryString: String = {
            bytes.toHexString.toUpperCase().map {
                case '0' => "0000"
                case '1' => "0001"
                case '2' => "0010"
                case '3' => "0011"
                case '4' => "0100"
                case '5' => "0101"
                case '6' => "0110"
                case '7' => "0111"
                case '8' => "1000"
                case '9' => "1001"
                case 'A' => "1010"
                case 'B' => "1011"
                case 'C' => "1100"
                case 'D' => "1101"
                case 'E' => "1110"
                case 'F' => "1111"
                case _ => ""
            }.mkString
        }

        /**
         * 字节数组转换为十六进制字符串
         *
         * @return
         */
        def toHexString: String = BytesUtils.toHex(bytes)

        /**
         * 字节数组转换为以:分隔每个字节的十六进制字符串
         *
         * @return
         */
        def toColonSeparatedHexString: String = bytes.map(byte => BytesUtils.toHex(Array(byte))).mkString(":")

        /**
         * 字节数组转换为Int
         *
         * @return
         */
        def toInt: Int = {
            if (bytes.length > 4)
                throw new IllegalArgumentException(s"Byte array with a length of ${bytes.length} can not be parse to int")
            if (bytes.length == 4)
                BytesUtils.toInt(bytes)
            else {
                var n = 0
                bytes.indices.foreach(i => {
                    n <<= 8
                    n ^= bytes(i) & 0xff
                })
                n
            }
        }

        /**
         * 字节数组转换为IP地址
         *
         * @return
         */
        def toIPAddress: String = {
            if (bytes.length != 4)
                throw new IllegalArgumentException(s"Byte array with a length of ${bytes.length} can not be parse to IPv4 address")
            bytes.map(Array(_).toShort).mkString(".")
        }

        /**
         * 字节数组转换为Short
         *
         * @return
         */
        def toShort: Short = {
            if (bytes.length > 2)
                throw new IllegalArgumentException(s"Byte array with a length of ${bytes.length} can not be parse to short")
            if (bytes.length == 2)
                BytesUtils.toShort(bytes)
            else
                (bytes(0) & 0xff).toShort
        }

    }

    implicit class BytesLengthFormatter(long: Long) {
        private val units = Array("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB")
        private val divisor = 1024

        /**
         * 把字节长度格式化为单位表示法
         *
         * @return
         */
        def toBytesLength: String = if (long <= 0) "0" else this.format(0, long)

        /**
         * 把字节长度格式化为单位表示法
         *
         * @param cursor 游标
         * @param is     得数
         * @return
         */
        @scala.annotation.tailrec
        private def format(cursor: Int, is: Double): String = {
            if (is >= divisor) {
                this.format(cursor + 1, is / divisor)
            } else {
                Mathematics.round(is, 2) + units(cursor)
            }
        }

    }

    implicit class BytesStringImplicits(string: String) {

        /**
         * 二进制字符串转字节
         *
         * @return
         */
        def binaryString2Bytes: Array[Byte] = {
            assert(string.length % 4 == 0, "Length of binary string must be times of 4")
            val hex = string.sliceByLength(4).map {
                case "0000" => '0'
                case "0001" => '1'
                case "0010" => '2'
                case "0011" => '3'
                case "0100" => '4'
                case "0101" => '5'
                case "0110" => '6'
                case "0111" => '7'
                case "1000" => '8'
                case "1001" => '9'
                case "1010" => 'A'
                case "1011" => 'B'
                case "1100" => 'C'
                case "1101" => 'D'
                case "1110" => 'E'
                case "1111" => 'F'
                case _ => '\0'
            }.mkString
            if (hex.length % 2 == 0)
                hex.hexString2Bytes
            else
                ("0" + hex).hexString2Bytes
        }

        /**
         * 16进制字符串转字节
         *
         * @return
         */
        def hexString2Bytes: Array[Byte] = hexString2Bytes(string)

        /**
         * 16进制字符串转字节数组
         *
         * @param hexString 16进制字符串
         * @return
         */
        private def hexString2Bytes(hexString: String) = {
            val hex = hexString.toUpperCase()
            val length = hex.length() / 2
            val hexChars = hex.toCharArray
            val buffer = mutable.ListBuffer[Byte]()
            var i = 0
            while (i < length) {
                val position = i * 2
                val x = charToByte(hexChars(position)) << 4
                val y = charToByte(hexChars(position + 1))
                val z = (x | y).toByte
                buffer += z
                i += 1
            }
            buffer.toArray
        }

        /**
         * 16进制字符转字节数组
         *
         * @param c 16进制字符
         * @return
         */
        private def charToByte(c: Char) = "0123456789ABCDEF".toCharArray.indexOf(c).toByte

    }

    implicit class IntImplicits(int: Int) {

        /**
         * Int转换为0x开头的十六进制表示
         *
         * @return
         */
        def toHex: String = this.toBytes.toHexString

        /**
         * Int转换为字节数组
         *
         * @return
         */
        def toBytes: Array[Byte] = BytesUtils.toBytes(int)

    }

    implicit class ShortImplicits(short: Short) {

        /**
         * Short转换为0x开头的十六进制表示
         *
         * @return
         */
        def toHexStringWith0X: String = toBytes.toHexStringWith0X

        /**
         * Short转换为字节数组
         *
         * @return
         */
        def toBytes: Array[Byte] = BytesUtils.toBytes(short)

    }

    implicit class SecondsImplicits(long: Long) {
        private val units = Map("en" -> Array(" days ", " hours ", " minutes ", " seconds"), "zh" -> Array("天", "小时", "分钟", "秒"))

        def longTimeFormat(lang: String): String = {
            val language = lang match {
                case "zh" => lang
                case _ => "en"
            }
            if (long >= 60) {
                val secondsRest = long % 60
                val minutes = long / 60
                if (minutes >= 60) {
                    val minutesRest = minutes % 60
                    val hours = minutes / 60
                    if (hours >= 24) {
                        val hoursRest = hours % 24
                        val day = hours / 24
                        day + units(language)(0) + hoursRest + units(language)(1) + minutesRest + units(language)(2) + secondsRest + units(language)(3)
                    } else {
                        hours + units(language)(1) + minutesRest + units(language)(2) + secondsRest + units(language)(3)
                    }
                } else {
                    minutes + units(language)(2) + secondsRest + units(language)(3)
                }
            } else {
                long + units(language)(3)
            }
        }

    }

}
