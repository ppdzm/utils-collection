package io.github.ppdzm.utils.universal.encryption

import java.io.{BufferedReader, ByteArrayInputStream, ByteArrayOutputStream, InputStreamReader}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

object GZipUtils {

    def compress(str: String): Array[Byte] = {
        if (str == null || str.length == 0)
            return null
        val obj = new ByteArrayOutputStream
        val gzip = new GZIPOutputStream(obj)
        gzip.write(str.getBytes("utf-8"))
        gzip.close()
        obj.toByteArray
    }

    def decompress(compressed: Array[Byte]): String = {
        val stringBuilder = new StringBuilder
        if (compressed != null && compressed.nonEmpty) {
            if (isCompressed(compressed)) {
                val bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressed)), "UTF-8"))
                var line = bufferedReader.readLine
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufferedReader.readLine
                }
            }
            else {
                stringBuilder.append(new String(compressed))
            }
        }
        stringBuilder.mkString
    }

    def isCompressed(compressed: Array[Byte]): Boolean = {
        (compressed(0) == GZIPInputStream.GZIP_MAGIC.toByte) &&
            (compressed(1) == (GZIPInputStream.GZIP_MAGIC >> 8).toByte)
    }

}