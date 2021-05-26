package io.github.ppdzm.utils.universal.base

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Properties

import io.github.ppdzm.utils.universal.cli.CliUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
object SQLAnalyser extends Logging {
    type Readable = {def readLine(hint: String): String}
    private lazy val pattern: Regex = """[#$]\{[^#\}$]+\}""".r

    /**
     * 解析单条SQL语句
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @param squeeze    是否压缩SQL
     * @return
     */
    def analyse(sql: String, properties: Properties, squeeze: Boolean): String = {
        val scripts = analyse(Array(sql), properties, squeeze)
        if (scripts.isEmpty)
            ""
        else
            scripts.head
    }

    def analyse(file: File, properties: Properties, squeeze: Boolean): Array[String] = {
        analyse(FileUtils.readLines(file, StandardCharsets.UTF_8).toList.toArray, properties, squeeze)
    }

    def analyse(originalScriptLines: Array[String], properties: Properties, squeeze: Boolean): Array[String] = {
        analyse(originalScriptLines, properties.toMap, squeeze)
    }

    def analyse(originalScriptLines: Array[String], properties: Map[String, AnyRef], squeeze: Boolean): Array[String] = {
        originalScriptLines
            .map(line => line.trimComment) //去掉每行注释后清除首尾空白字符
            .filter(_.nonEmpty) //筛掉空行
            .mkString(if (squeeze) " " else "\n") //所有行用空格/换行符区分加在一起
            .split(";") //用分号做分隔符，截取成多个语句
            .map(this.substitute(_, properties)) //替换变量
            .map(_.trim) //清除变量替换完成后每行的首尾空白字符
            .map(s => if (squeeze) this.squeeze(s) else s)
            .filter(_.nonEmpty) //筛掉空行
    }

    /**
     * 压缩SQL脚本
     *
     * @param script SQL脚本
     * @return
     */
    def squeeze(script: String): String = {
        script
            .replace("\b", "")
            .replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
            .replace("`", "")
            .trim
            .replaceAllLiterally("  ", "")
            .replace("( ", "(")
            .replace(" )", ")")
            .replace(", ", ",")
            .replace(" ,", ",")
    }

    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return
     */
    def substitute(sql: String, properties: Map[String, AnyRef]): String = {
        var temp = sql
        var canNotFindMore = false
        while (this.pattern.findFirstMatchIn(temp).isDefined && !canNotFindMore) {
            canNotFindMore = true
            this.pattern.findAllMatchIn(temp).foreach {
                m =>
                    val matcher = m.group(0)
                    val name = matcher.substring(2, matcher.length - 1).replace("hivevar:", "")
                    if (!properties.contains(name)) {
                        throw new Exception(s"value of parameter $name isn't provided")
                    }
                    val value = properties(name).toString
                    if (value != null) {
                        temp = temp.replace(matcher, value)
                        canNotFindMore = false
                    }
            }
        }
        temp
    }

    /**
     * 压缩SQL脚本
     *
     * @param scripts SQL脚本(s)
     * @return
     */
    def squeeze(scripts: Array[String]): Array[String] = scripts.map(squeeze)

    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return
     */
    def substitute(sql: String, properties: Properties): String = substitute(sql, properties.toMap)

    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return
     */
    def substitute(sql: String, properties: Properties, readParameterFromConsole: Boolean, readable: Readable, hint: String): String = {
        if (sql.isNull)
            null
        else {
            var temp = sql
            var canNotFindMore = false
            val listBuffer = ListBuffer[String]()
            var inputParameterCount = 0
            while (this.pattern.findFirstMatchIn(temp).isDefined && !canNotFindMore) {
                canNotFindMore = true
                this.pattern.findAllMatchIn(temp).foreach(m => {
                    val matcher = m.group(0)
                    val name = matcher.substring(2, matcher.length - 1).replace("hivevar:", "")
                    val value = properties.getProperty(name)
                    if (value != null) {
                        temp = temp.replace(matcher, value)
                        canNotFindMore = false
                    } else if (readParameterFromConsole && !listBuffer.contains(name)) {
                        listBuffer += name
                        val parameterValue = readable.readLine(hint)
                        inputParameterCount += 1
                        if (parameterValue.notNullAndEmpty) {
                            temp = temp.replace(matcher, parameterValue)
                            properties.put(name, parameterValue)
                        }
                    }
                })
            }
            if (inputParameterCount > 0)
                CliUtils.deleteRowsUpward(inputParameterCount)
            temp
        }
    }


}
