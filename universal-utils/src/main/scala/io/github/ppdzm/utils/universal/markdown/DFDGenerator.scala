package io.github.ppdzm.utils.universal.markdown

import io.github.ppdzm.utils.universal.base.SqlUtils
import org.apache.commons.io.FileUtils

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Properties
import scala.collection.JavaConversions._

object DFDGenerator extends App {
    private val variableRegex = "set (?<variable>.*?)=(?<value>.*?)$".r
    private val pattern = """[#\$]\{[^#\}\$]+\}""".r
    private val sourceRegex = "(from|join) (?<table>[a-zA-Z]{1}[^ ]*?\\.[^ ]+)".r("op", "table")
    private val temporaryRegex = " (?<table>tmp\\..+?) ".r("table")
    private val destinationRegex = "insert (overwrite|into) table (?<table>[a-zA-Z]{1}.*?) ".r("op", "table")
    private val destinationRegex2 = "create table (?<table>[a-zA-Z]{1}.*?) ".r("table")
    private val sqoopRegex = "--table[ ]*(?<m>[^ ]+).*hcatalog-database[ ]*(?<d>[^ ]+).*hcatalog-table[ ]*(?<h>[^ ]+)".r("m", "d", "h")

    new File("/Users/stuartalex/Projects/Python/wwj-hive-warehouse/launch-pad/skb")
      .listFiles()
      .foreach {
          file => generate(file, overwrite = true)
      }

    def generate(file: File, overwrite: Boolean): Unit = {
        if (file.isDirectory) {
            val directory = file.getAbsolutePath
            val filesAndDirectories = file.listFiles().sortWith((a, b) => a.getName < b.getName)
            //存储sqoop命令的文件
            val sqoopCommandFile = filesAndDirectories.filter(_.isFile).find(_.getName == "sqoop_cmd.txt").getOrElse {
                file.getParentFile.listFiles().filter(_.isFile).find(_.getName == "sqoop_cmd.txt").orNull
            }
            //分析Hive表——MySQL表的关系
            val hiveMySQLMapping = analyseSqoopScript(sqoopCommandFile)
            //mysql表名和编号的映射
            val mySQLMapping = hiveMySQLMapping.values.toList.zipWithIndex.map(z => z._1 -> s"M${z._2 + 1}").toMap
            //mysql子图文本
            val mySQLMarkdown = if (mySQLMapping.nonEmpty)
                mySQLMapping.map(m => s"${m._1}").mkString("subgraph MySQL\n", "\n", "\nend\n")
            //                mySQLMapping.map(m => s"${m._2}[${m._1}]").mkString("subgraph MySQL\n", "\n", "\nend\n")
            else
                ""
            //所有需要分析的HQL脚本
            val hqlFiles = filesAndDirectories.filter(_.isFile).filter(_.getName.endsWith(".sql")).filterNot(_.getName.startsWith("ignore")).filterNot(_.getName == "create_table.sql")
            //子图文本，每个HQL脚本一个子图，子图内又分源表、临时表、目标表三个子图
            val subgraphs = hqlFiles.indices.map(i => analyseHQLScript(hiveMySQLMapping, mySQLMapping, hqlFiles(i), i)).mkString("", "\n", "\n")
            val markdownText = "```mermaid\ngraph LR\n" + subgraphs + mySQLMarkdown + "```"
            val dfd = new File(directory + "/dfd.md")
            //dfd.md不存在或允许覆盖则将markdown文本写入到dfd.md
            if (!dfd.exists() || overwrite) {
                println(markdownText)
                FileUtils.write(dfd, markdownText, StandardCharsets.UTF_8)
            }
            //对当前文件夹下的文件夹做递归操作
            filesAndDirectories.filter(_.isDirectory).foreach(this.generate(_, overwrite))
        }
    }

    private def analyseHQLScript(hiveMySQLMapping: Map[String, String], mySQLMapping: Map[String, String], file: File, group: Int) = {
        val scripts = SqlUtils.analyse(file, new Properties, true, false).map(_.toLowerCase)
        //当前脚本中涉及的临时表
        val temporaryTables = scripts.flatMap(this.temporaryRegex.findAllMatchIn(_).map(_.group("table"))).sorted.toSet
        //临时表及编号映射
        val temporaryMapping = temporaryTables.zipWithIndex.map(z => z._1 -> s"T${group + 1}${z._2 + 1}").toMap
        //当前脚本中涉及的目标表
        val destinationTables = scripts.flatMap(this.destinationRegex.findAllMatchIn(_).map(_.group("table"))).sorted.toSet.diff(temporaryTables)
        //目标表及编号映射
        val destinationMapping = destinationTables.zipWithIndex.map(z => z._1 -> s"D${group + 1}${z._2 + 1}").toMap
        //当前脚本中涉及的源表
        val sourceTables = scripts.flatMap(this.sourceRegex.findAllMatchIn(_).map(_.group("table").replace(")", ""))).sorted.toSet.diff(destinationTables).diff(temporaryTables)
        //源表及编号映射
        val sourceMapping = sourceTables.zipWithIndex.map(z => z._1 -> s"S${group + 1}${z._2 + 1}").toMap
        //源表markdown文本
        val sourcesMarkdown = if (sourceMapping.nonEmpty)
        //            sourceMapping.map(s => s"${s._2}[${s._1}]").mkString("\n", "\n", "\n\n")
            sourceMapping.map(s => s"${s._1}").mkString("\n", "\n", "\n\n")
        else
            ""
        //临时表markdown文本
        val temporariesMarkdown = if (temporaryMapping.nonEmpty)
            temporaryMapping.map(t => s"${t._1}").mkString("\n", "\n", "\n\n")
        //            temporaryMapping.map(t => s"${t._2}[${t._1}]").mkString("\n", "\n", "\n\n")
        else
            ""
        //目标表markdown文本
        val destinationsMarkdown = if (destinationMapping.nonEmpty)
            destinationMapping.map(d => s"${d._1}").mkString("\n", "\n", "\n\n")
        //            destinationMapping.map(d => s"${d._2}[${d._1}]").mkString("\n", "\n", "\n\n")
        else
            ""
        //有向边集合
        val edges = scripts.filterNot(_.startsWith("alter")).filterNot(_.startsWith("drop")).flatMap(script => {
            //目标表，insert overwrite/into后面的表
            val destinations = this.destinationRegex.findAllMatchIn(script).map(_.group("table")).toSet
            //临时表，create table tmp.后面的表
            val temporaries = this.destinationRegex2.findAllMatchIn(script).map(_.group("table")).toSet
            //源表，join/from后面的表
            val sources = this.sourceRegex.findAllMatchIn(script).map(_.group("table")).toSet
            //源表-->临时表
            val ste = sources.filter(sourceMapping.isDefinedAt)
              //                .map(sourceMapping(_))
              .flatMap(s => temporaries.filter(temporaryMapping.isDefinedAt).map(t => s"$s-->$t"))
            //                .flatMap(s => temporaries.filter(temporaryMapping.isDefinedAt).map(temporaryMapping(_)).map(t => s"$s-->$t"))
            //临时表作为源表的，临时表-->临时表
            val tte = sources.filter(temporaryMapping.isDefinedAt)
              //                .map(temporaryMapping(_))
              .flatMap(s => temporaries.filter(temporaryMapping.isDefinedAt).map(t => s"$s-->$t"))
            //                .flatMap(s => temporaries.filter(temporaryMapping.isDefinedAt).map(temporaryMapping(_)).map(t => s"$s-->$t"))
            //源表-->目标表
            val sde = sources.filter(sourceMapping.isDefinedAt)
              //                .map(sourceMapping(_))
              .flatMap(s => destinations.filter(destinationMapping.isDefinedAt).map(d => s"$s-->$d"))
            //                .flatMap(s => destinations.filter(destinationMapping.isDefinedAt).map(destinationMapping(_)).map(d => s"$s-->$d"))
            //临时表作为源表的，临时表-->目标表
            val tde = sources.filter(temporaryMapping.isDefinedAt)
              //                .map(temporaryMapping(_))
              .flatMap(t => destinations.filter(destinationMapping.isDefinedAt).map(d => s"$t-->$d"))
            //                .flatMap(t => destinations.filter(destinationMapping.isDefinedAt).map(destinationMapping(_)).map(d => s"$t-->$d"))
            ste ++ tte ++ sde ++ tde
        }) ++
          //指向MySQL的有向边为粗体
          destinationTables.filter(hiveMySQLMapping.isDefinedAt).map(d => destinationMapping(d) + "==>" + mySQLMapping(hiveMySQLMapping(d)))
        s"subgraph ${file.getName}\n" + sourcesMarkdown + temporariesMarkdown + destinationsMarkdown + "end\n" + edges.sorted.toSet.mkString("\n")
    }

    private def analyseSqoopScript(file: File) = {
        if (file != null) {
            val sqoops = FileUtils.readLines(file, StandardCharsets.UTF_8).filterNot(_.isEmpty).mkString("\n").replace("\\\n", "").split("\n")
            sqoops.flatMap(this.sqoopRegex.findAllMatchIn).map(m => (m.group(2) + "." + m.group(3), m.group(1))).toMap
        }
        else
            Map[String, String]()
    }

}
