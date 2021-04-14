package io.github.ppdzm.utils.hadoop.hdfs

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

class HDFSHandler(nameNodeAddress: String) extends FileSystemHandler {
    override protected val fileSystem: FileSystem = FileSystem.get(configuration)
    configuration.set("fs.defaultFS", nameNodeAddress)
    configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
    private val configuration = new Configuration()
}
