package io.github.ppdzm.utils.hadoop.scala.hdfs

import org.scalatest.FunSuite

class HDFSTest extends FunSuite {

    test("HA-HDFS-write-and-append") {
        val hdfsHandler = new HighAvailableHDFSHandler("nexus", "nn1,nn2".split(","), 8020)
        hdfsHandler.listDirectories("/", recursive = false).foreach(println)
        hdfsHandler.delete("a1.txt")
        hdfsHandler.touch("a1.txt")
        hdfsHandler.writeLine("123".getBytes, "a1.txt")
        hdfsHandler.appendLine("456".getBytes, "a1.txt")
        hdfsHandler.appendLine("789", "a1.txt")
        hdfsHandler.appendLine("xyz", "a1.txt")
    }

}
