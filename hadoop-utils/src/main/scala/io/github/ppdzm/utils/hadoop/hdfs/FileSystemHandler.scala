package io.github.ppdzm.utils.hadoop.hdfs

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets._
import java.util.concurrent.TimeoutException

import io.github.ppdzm.utils.universal.base.LoggingTrait
import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.apache.hadoop.fs.{FileSystem, LocatedFileStatus, Path}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by Stuart Alex on 2017/1/11.
 */
trait FileSystemHandler extends LoggingTrait {
    protected val fileSystem: FileSystem

    /**
     * 向指定路径追加文本后换行
     *
     * @param line 文本
     * @param path 路径
     */
    def appendLine(line: String, path: String): Unit = {
        appendLine(line.getBytes(UTF_8), path)
    }

    /**
     * 向指定路径追加字节数组后换行
     *
     * @param bytes 字节数组
     * @param path  路径
     */
    def appendLine(bytes: Array[Byte], path: String): Unit = {
        append(bytes.++(System.lineSeparator().getBytes(UTF_8)), path)
    }

    /**
     * 向指定路径追加多行文本
     *
     * @param lines 多行文本
     * @param path  路径
     */
    def appendLines(lines: Array[String], path: String): Unit = {
        append(lines.mkString(System.lineSeparator()), path)
    }

    /**
     * 向指定路径追加文本
     *
     * @param line 文本
     * @param path 路径
     */
    def append(line: String, path: String): Unit = {
        append(line.getBytes(UTF_8), path)
    }

    /**
     * 向指定路径追加多行文本
     *
     * @param lines 字节数组的数组
     * @param path  路径
     */
    def appendLines(lines: Array[Array[Byte]], path: String): Unit = {
        val head = lines.head
        // 在除第一行外的其他行首加上换行符
        val tail = lines.tail.flatMap { bytes => Array.concat(System.lineSeparator().getBytes(UTF_8), bytes) }
        append(Array.concat[Byte](head, tail), path)
    }

    /**
     * 向指定路径追加字节数组
     *
     * @param bytes 字节数组
     * @param path  路径
     */
    def append(bytes: Array[Byte], path: String): Unit = {
        LoanPattern.using(fileSystem.append(new Path(path))) {
            fsDataOutputStream =>
                fsDataOutputStream.write(bytes)
        }
    }

    /**
     * 删除指定路径（文件或文件夹）
     *
     * @param path 路径（文件或文件夹）
     * @return
     */
    def delete(path: String): Boolean = this.fileSystem.delete(new Path(path), true)

    def listFiles(path: String, recursive: Boolean): List[String] = {
        listFileStatus(path, recursive).filter(_.isFile).map(_.getPath.getName)
    }

    def listDirectories(path: String, recursive: Boolean): List[String] = {
        listFileStatus(path, recursive).filter(_.isDirectory).map(_.getPath.getName)
    }

    private def listFileStatus(path: String, recursive: Boolean): List[LocatedFileStatus] = {
        val listBuffer = mutable.ListBuffer[LocatedFileStatus]()
        val remoteIterator = this.fileSystem.listFiles(new Path(path), recursive)
        while (remoteIterator.hasNext) {
            val fileStatus = remoteIterator.next()
            listBuffer.add(fileStatus)
        }
        listBuffer.toList
    }

    /**
     * 创建新的路径
     *
     * @param path 路径名称
     * @return
     */
    def mkdirs(path: String): Boolean = this.fileSystem.mkdirs(new Path(path))

    /**
     * 从指定路径读取数据
     *
     * @param path 路径
     * @return
     */
    def read(path: String): String = {
        LoanPattern.using(this.fileSystem.open(new Path(path))) {
            fsDataInputStream => {
                val bytes = new Array[Byte](fsDataInputStream.available())
                fsDataInputStream.read(bytes)
                new String(bytes, StandardCharsets.UTF_8)
            }
        }
    }

    def touch(fileName: String): Boolean = {
        val filePath = new Path(fileName)
        if (!fileSystem.exists(filePath.getParent))
            fileSystem.mkdirs(filePath.getParent)
        if (!fileSystem.exists(filePath))
            fileSystem.createNewFile(filePath)
        true
    }

    /**
     * @param path    路径
     * @param timeout second
     */
    def waitUntilPathExists(path: String, timeout: Int): Unit = {
        val startTime = System.currentTimeMillis()
        while (!exists(path)) {
            Thread.sleep(1000)
            if ((System.currentTimeMillis() - startTime) / 1000 > timeout)
                throw new TimeoutException(s"$path still not exist after $timeout seconds")
        }
    }

    /**
     * 等待所有路径（文件或文件夹）存在
     *
     * @param paths   （1个或多个）路径（文件或文件夹）
     * @param timeout 超时
     */
    def waitUntilPathExists(paths: List[String], timeout: Int): Unit = {
        val startTime = System.currentTimeMillis()
        while (paths.forall(exists)) {
            val notExistPaths = paths.filterNot(exists).mkString(";")
            logInfo(s"$notExistPaths still not exist")
            Thread.sleep(1000)
            if ((System.currentTimeMillis() - startTime) / 1000 > timeout) {
                throw new Exception(s"$notExistPaths still not exist after $timeout seconds")
            }
        }
    }

    /**
     * 判断指定路径（文件或文件夹是否存在）
     *
     * @param path 路径（文件或文件夹）
     * @return
     */
    def exists(path: String): Boolean = this.fileSystem.exists(new Path(path))

    /**
     * 向指定路径写入字节数组后换行
     *
     * @param bytes 字节数组
     * @param path  路径
     */
    def writeLine(bytes: Array[Byte], path: String): Unit = {
        write(bytes.++(System.lineSeparator().getBytes(UTF_8)), path)
    }

    /**
     * 向指定路径写入文本后换行
     *
     * @param line 文本
     * @param path 路径
     */
    def writeLine(line: String, path: String): Unit = {
        write((line + System.lineSeparator()).getBytes(UTF_8), path)
    }

    /**
     * 向指定路径写入多行文本
     *
     * @param lines 多行文本
     * @param path  路径
     */
    def writeLines(lines: Array[String], path: String): Unit = {
        write(lines.mkString(System.lineSeparator()), path)
    }

    /**
     * 向指定路径写入文本
     *
     * @param line 文本
     * @param path 路径
     */
    def write(line: String, path: String): Unit = {
        write(line.getBytes(UTF_8), path)
    }

    /**
     * 向指定路径写入字节数组
     *
     * @param bytes 字节数组
     * @param path  路径
     */
    def write(bytes: Array[Byte], path: String): Unit = {
        LoanPattern.using(this.fileSystem.create(new Path(path))) {
            fsDataOutputStream =>
                fsDataOutputStream.write(bytes)
                fsDataOutputStream.flush()
        }
    }

    /**
     * 向指定路径写入多行文本
     *
     * @param lines 字节数组的数组
     * @param path  路径
     */
    def writeLines(lines: Array[Array[Byte]], path: String): Unit = {
        val head = lines.head
        // 在除第一行外的其他行首加上换行符
        val tail = lines.tail.map { bytes => Array.concat(System.lineSeparator().getBytes(UTF_8), bytes) }
        write(Array.concat[Byte](head, Array.concat(tail: _*)), path)
    }

}
