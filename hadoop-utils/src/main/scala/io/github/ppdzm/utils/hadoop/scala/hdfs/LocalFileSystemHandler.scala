package io.github.ppdzm.utils.hadoop.scala.hdfs

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

object LocalFileSystemHandler extends FileSystemHandler {
    override protected val fileSystem: FileSystem = FileSystem.getLocal(new Configuration())
}
