package io.github.ppdzm.utils.universal.config

import io.github.ppdzm.utils.universal.cli.MessageGenerator
import io.github.ppdzm.utils.universal.core.CoreConstants._
import io.github.ppdzm.utils.universal.core.{CoreConstants, SystemProperties}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import jline.console.ConsoleReader

import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2021/3/15.
 */
object FileConfigPrinter extends App {
    //private val maxValueLength = configs.map(_._2.length).max
    private val consoleReader = new ConsoleReader()
    private val prefix = consoleReader.readLine("请输入配置文件前缀（默认为".green + "application".red + "）: ".green)
    private val active = consoleReader.readLine("请输入配置文件后缀（默认为".green + "空".red + "）:".green)
    private val extension = consoleReader.readLine("请输入配置文件扩展名 (默认为".green + ".properties".red + "）:".green)
    if (prefix.nonEmpty)
        System.setProperty(PROFILE_PREFIX_KEY, prefix)
    if (active.nonEmpty)
        System.setProperty(PROFILE_ACTIVE_KEY, active)
    if (extension.nonEmpty)
        System.setProperty(PROFILE_EXTENSION_KEY, extension)
    private val config = new FileConfig()
    private val configKeyValuePairList = config.properties.toList
    if (configKeyValuePairList.isEmpty) {
        println("configs is empty, exit!".red)
        sys.exit(0)
    }
    private val lang = SystemProperties.language
    private val tip = lang match {
        case "zh" => "提示：当前显示语言为中文，欲显示其他语言请使用java -Dprogram.language=<lang>切换，目前仅支持en、zh。"
        case _ => "Tip: the current displayed language is English. To display other languages, please use java -Dprogram.language=<lang> to change, only en, zh is supported now."
    }
    private val maxKeyLength = configKeyValuePairList.map(_._1.length).max
    private val profileTip = if (active.isEmpty) s"${"default".red} profile" else s"profile ${active.red}"
    private val profileTipLength = {
        if (active.isEmpty) "default profile" else s"profile $active"
    }.length + 9
    private val paddedDescriptionTip = "description".pad(profileTipLength, ' ', -1)
    private val messages =
        configKeyValuePairList
            .map { case (k, v) => (k, v, MessageGenerator.generate(lang, k)) }
            .filterNot(_._1 == CoreConstants.PROFILE_PATH_KEY)
            .sortBy(_._1)
            .map {
                case (k, v, m) =>
                    s"${k.green}${Array.fill(maxKeyLength - k.length)("─").mkString}┬─value in $profileTip: ${v.cyan} \n${Array.fill(maxKeyLength)(" ").mkString}└─$paddedDescriptionTip: ${m.cyan}"
            }
    tip.prettyPrintln("0;32")
    println()
    messages.foreach(println)
}
