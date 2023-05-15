package io.github.ppdzm.utils.hadoop.scala.hdfs

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

class HDFSHandler(nameNodeAddress: String) extends FileSystemHandler {
    private val configuration = new Configuration()
    configuration.set("fs.defaultFS", nameNodeAddress)
    configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
    override protected val fileSystem: FileSystem = FileSystem.get(configuration)
}
