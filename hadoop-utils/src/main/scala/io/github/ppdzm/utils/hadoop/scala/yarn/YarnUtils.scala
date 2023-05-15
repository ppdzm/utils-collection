package io.github.ppdzm.utils.hadoop.scala.yarn

import java.io.File
import java.nio.charset.StandardCharsets
import java.text.MessageFormat
import java.util.UUID
import com.fasterxml.jackson.databind.JsonNode
import io.github.ppdzm.utils.universal.cli.{MessageGenerator, Render}
import io.github.ppdzm.utils.universal.feature.ExceptionGenerator
import io.github.ppdzm.utils.universal.formats.json.JacksonJsonUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringEscapeUtils
import scalikejdbc.LoanPattern

import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

/**
 * Created by Stuart Alex on 2017/9/6.
 * 通过Yarn Resource Manager API查看Yarn APP的日志
 * 1. 通过yarn-rm:8088/ws/v1/cluster/apps获取所有yarn app的json（嵌套为apps-app）或通过yarn-rm:8088/ws/v1/cluster/apps/${app-id}获取指定yarn app的json（嵌套为app)
 * 2. 从json中找到id，形如：application_1562136147484_25556
 * 3. 通过yarn-rm:8088/proxy/${app-id}/api/v1/applications/${app-id}/allexecutors获取指定yarn app的executors json（返回json为List）
 */
object YarnUtils {
    private lazy val fieldMsgFormatter = MessageFormat.format(s"Cannot find field {0} from json\n{1}", _: String, _: String)
    private lazy val saveMsgFormatter = MessageFormat.format(s"Page details of executors is saved to file {0}, please download this file and acquire log address of specific executor, then use curl to see the logs", _: String)
    private lazy val noLogsMsg = MessageFormat.format(s"Cannot find any log in html:\n{0}", _: String)

    /**
     * 获取Driver日志
     *
     * @param id         Spark应用id
     * @param length     获取日志的长度
     * @param outputType 获取日志的类型
     * @return
     */
    def driverLogs(id: String, length: String, outputType: OutputType.Value = OutputType.err, resourceManagerAddresses: Array[String]): String = {
        val json = this.sparkAppJson(id, resourceManagerAddresses)
        val amContainerLogsAddress = new Regex(""""amContainerLogs":"(.*?)"""", "url").findFirstMatchIn(json)
            .getOrElse(throw new NoSuchFieldException(this.fieldMsgFormatter("amContainerLogs", JacksonJsonUtils.pretty(json)))).group("url")
        this.acquireLogs(s"$amContainerLogsAddress/$outputType/?start=$length")
    }

    /**
     * 从url获取网页源码，解析日志正文
     *
     * @param url 网页地址
     * @return
     */
    private def acquireLogs(url: String) = {
        val html = StringEscapeUtils.unescapeHtml4(LoanPattern.using(Source.fromURL(url, "utf-8"))(_.mkString))
        new Regex("""<pre>([\s\S]*?)</pre>""", "log").findFirstMatchIn(html)
            .orElse(new Regex("""<td class="content">[^<]*?<h1>([\s\S]*?)</h1>""", "log").findFirstMatchIn(html))
            .getOrElse(throw new NoSuchElementException(this.noLogsMsg(html))).group("log").trim
    }

    /**
     * 获取Executor日志
     *
     * @param address    日志主地址
     * @param length     获取日志的长度
     * @param outputType 获取日志的类型
     * @return
     */
    def executorLogs(address: String, length: String, outputType: OutputType.Value = OutputType.err): String = {
        this.acquireLogs(s"$address/root/$outputType/?start=$length")
    }

    /**
     * 获取executors详情
     *
     * @param id    Spark应用id
     * @param state Execute状态
     * @return
     */
    def executorsV1(id: String, state: ExecutorState.Value, resourceManagerAddresses: Array[String]): (List[String], List[List[String]]) = {
        val html = this.executorsPageHtml(id, resourceManagerAddresses)
        val regex = state match {
            case ExecutorState.active => """<td>([^<]*?)<[^<]*?<td>([^<]*?)<[^<]*?<.*?key="(Active)">[\s\S]*?<a href="(.*?)/root.*?">"""
            case ExecutorState.both => """<td>([^<]*?)<[^<]*?<td>([^<]*?)<[^<]*?<.*?key="([(Dead)+|(Active)+]+)">[\s\S]*?<a href="(.*?)/root.*?">"""
            case ExecutorState.dead => """<td>([^<]*?)<[^<]*?<td>([^<]*?)<[^<]*?<.*?key="(Dead)">[\s\S]*?<a href="(.*?)/root.*?">"""
            case unsupported => throw new IllegalArgumentException(s"executor.status $unsupported is unsupported")
        }
        val columns = List(
            MessageGenerator.generate("column-number"),
            "Executor名称",
            MessageGenerator.generate("column-status"),
            MessageGenerator.generate("column-address")
        )
        (columns, new Regex(regex, columns: _*).findAllMatchIn(html).filter(m => Try(m.group("序号").toInt).isSuccess)
            .map(m => columns.map(m.group)).toList.sortWith((a: List[String], b: List[String]) => a.head.toInt < b.head.toInt))
    }

    /**
     * 返回executors详情
     *
     * @param id    Spark应用id
     * @param state Execute状态
     * @return
     */
    def executorsV2(id: String, state: ExecutorState.Value, resourceManagerAddresses: Array[String]): (List[String], List[List[String]]) = {
        val json = this.executorsJson(id, resourceManagerAddresses)
        val columnsForDisplay = List(
            MessageGenerator.generate("column-order-number"),
            MessageGenerator.generate("column-executor-address"),
            MessageGenerator.generate("column-is-active"),
            MessageGenerator.generate("column-log-address"))
        val rows = JacksonJsonUtils.parse(json).toList
            .filter(_.get("id").asText() != "driver")
            .filter(node => {
                state match {
                    case ExecutorState.active => node.get("isActive").asBoolean()
                    case ExecutorState.both => true
                    case ExecutorState.dead => !node.get("isActive").asBoolean()
                }
            })
            .map(node => {
                val outAddress = node.get("executorLogs").get("stdout").asText().split("/")
                val logAddress = outAddress.slice(0, outAddress.length - 2).mkString("/")
                List(node.get("id").asText(), node.get("hostPort").asText(), node.get("isActive").asText(), logAddress)
            })
            .sortBy(_.head)
        (columnsForDisplay, rows)
    }

    /**
     * 获取executors详情json
     *
     * @param id yarn application id
     * @return
     */
    def executorsJson(id: String, resourceManagerAddresses: Array[String]): String = {
        val trackingUrl = findTrackingUrl(id, resourceManagerAddresses)
        LoanPattern.using(Source.fromURL(s"${trackingUrl}api/v1/applications/$id/allexecutors", "utf-8"))(_.mkString)
    }

    /**
     * 找到tracking url
     *
     * @param id                       yarn application id
     * @param resourceManagerAddresses resource manager地址
     * @return
     */
    def findTrackingUrl(id: String, resourceManagerAddresses: Array[String]): String = {
        val json = this.sparkAppJson(id, resourceManagerAddresses)
        new Regex(""""trackingUrl":"(.*?)"""", "url")
            .findFirstMatchIn(json)
            .getOrElse(throw new NoSuchFieldException(this.fieldMsgFormatter("trackingUrl", JacksonJsonUtils.pretty(json)))).group("url")
    }

    /**
     * Spark应用详情Json
     *
     * @param id Spark应用id
     * @return
     */
    def sparkAppJson(id: String, resourceManagerAddresses: Array[String]): String = {
        this.checkID(id)
        val attachableAddress = findAttachableUrl(resourceManagerAddresses)
        LoanPattern.using(Source.fromURL(s"http://$attachableAddress/ws/v1/cluster/apps/$id", "utf-8"))(_.mkString)
    }

    /**
     * 检查id的合法性
     *
     * @param id Spark应用id
     */
    private def checkID(id: String): Unit = assert(id.matches("""application_\d+_\d+"""), s"$id is not correct Yarn Application format")

    /**
     * 找到可连接的url
     *
     * @param urls 一组url
     * @return
     */
    def findAttachableUrl(urls: Array[String]): String = {
        val attachableUrl = urls.find(url => Try(Source.fromURL(s"http://$url", "utf8")).isSuccess)
        if (attachableUrl.isEmpty)
            throw ExceptionGenerator.newException("NoAttachableUrl", "no attachable resource manager found")
        attachableUrl.get
    }

    /**
     * 根据关键字和应用状态搜索Spark应用
     *
     * @param keyWord 关键字
     * @param state   应用状态
     * @return
     */
    def sparkApps(keyWord: String, resourceManagerAddresses: Array[String], state: ApplicationState.Value = ApplicationState.all): List[JsonNode] = {
        JacksonJsonUtils
            .parse(this.allYarnApps(resourceManagerAddresses))
            .get("apps")
            .get("app")
            .elements()
            .filter(_.get("applicationType").asText.toLowerCase == "spark")
            .filter(e => e.get("name").asText.matches(keyWord) || e.get("name").asText.contains(keyWord))
            .filter(e => {
                if (state == ApplicationState.all)
                    true
                else
                    e.get("state").asText.toLowerCase == state.toString.toLowerCase
            })
            .toList
            .sortWith((a: JsonNode, b: JsonNode) => this.sort(a.get("id").asText, b.get("id").asText))
    }

    /**
     * 获取所有Yarn Application
     *
     * @return
     */
    private def allYarnApps(resourceManagerAddresses: Array[String]): String = {
        val attachableAddress = findAttachableUrl(resourceManagerAddresses)
        LoanPattern.using(Source.fromURL(s"http://$attachableAddress/ws/v1/cluster/apps", "utf-8"))(_.mkString)
    }

    /**
     * 两个Spark应用id比较大小
     *
     * @param a Spark应用id
     * @param b Spark应用id
     * @return
     */
    def sort(a: String, b: String): Boolean = {
        val x = a.split("_")
        val y = b.split("_")
        x(1).toLong < y(1).toLong || (x(1) == y(1) && x(2).toInt < y(2).toInt)
    }

    /**
     * 保存Executors页面的html
     *
     * @param id Spark应用id
     */
    def saveExecutorsPage(id: String, resourceManagerAddresses: Array[String]): Unit = {
        val html = this.executorsPageHtml(id, resourceManagerAddresses)
        val file = new File("/tmp/" + UUID.randomUUID().toString + ".html")
        FileUtils.write(file, html, StandardCharsets.UTF_8)
        this.saveMsgFormatter(file.getAbsolutePath).prettyPrintln(Render.GREEN)
    }

    /**
     * 获取Executors页面的html
     *
     * @param id Spark应用id
     * @return
     */
    def executorsPageHtml(id: String, resourceManagerAddresses: Array[String]): String = {
        val trackingUrl = findTrackingUrl(id, resourceManagerAddresses)
        LoanPattern.using(Source.fromURL(s"$trackingUrl/executors", "utf-8"))(_.mkString)
    }
}