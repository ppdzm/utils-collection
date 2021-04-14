package io.github.ppdzm.utils.universal.base

/**
 * Created by Stuart Alex on 2021/4/6.
 */
object BytesUtils {
    private val HEX_CHARS = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    def toBytes(value: Short): Array[Byte] = {
        val bytes = Array[Byte](0, value.toByte)
        bytes(0) = (value >> 8).toShort.toByte
        bytes
    }

    def toBytes(value: Int): Array[Byte] = {
        val bytes = new Array[Byte](4)
        var tmp = value
        for (i <- 3 until 0 by -1) {
            bytes(i) = tmp.toByte
            tmp >>>= 8
        }
        bytes(0) = tmp.toByte
        bytes
    }

    def toInt(bytes: Array[Byte]): Int = toInt(bytes, 0, 4)

    def toInt(bytes: Array[Byte], offset: Int, length: Int): Int = {
        var int = 0
        for (i <- offset until (offset + length)) {
            int <<= 8
            int ^= bytes(i) & 255
        }
        int
    }

    def toHex(bytes: Array[Byte]): String = toHex(bytes, 0, bytes.length)

    def toHex(bytes: Array[Byte], offset: Int, length: Int): String = {
        val numChars = bytes.length * 2
        val charArray = new Array[Char](numChars)
        var cursor = 0
        while (cursor < numChars) {
            val byte = bytes(offset + cursor / 2)
            charArray(cursor) = HEX_CHARS((byte >> 4) & 15)
            charArray(cursor + 1) = HEX_CHARS(byte & 15)
            cursor += 2
        }
        new String(charArray)
    }

    def toShort(bytes: Array[Byte]): Short = toShort(bytes, 0, 2)

    def toShort(bytes: Array[Byte], offset: Int, length: Int): Short = {
        var short: Short = 0
        short = ((short ^ bytes(offset)) & 255).toShort
        short = (short << 8).toShort
        short = ((short ^ bytes(offset + 1)) & 255).toShort
        short
    }

}
