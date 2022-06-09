package io.github.ppdzm.utils.universal.cli

import io.github.ppdzm.utils.universal.base.DateTimeUtils
import io.github.ppdzm.utils.universal.base.Symbols.carriageReturn
import io.github.ppdzm.utils.universal.implicits.BasicConversions._

object ExtendedCliUtils extends CliUtils {

    /**
     * 渲染文本
     *
     * @param messages 原始文本与渲染器
     * @return String
     */
    def rendering(messages: Array[(String, Render)]): String = {
        messages
            .map {
                case (msg, render) => CliUtils.rendering(msg, render)
            }
            .mkString
    }

    /**
     * 打印输出帮助文档
     *
     * @param help 命令——帮助
     */
    def printHelp(help: Array[(String, String)], render: String): Unit = this.printHelp(help.toList, render)

    /**
     * 打印输出帮助文档
     *
     * @param help 命令——帮助
     */
    def printHelp(help: Seq[(String, String)], render: String): Unit = {
        val maxLength = help.map(_._1).map(_.length).max
        help.map {
            e =>
                val parts = e._2.split("\n")
                if (parts.length > 1)
                    e._1.pad(maxLength, ' ', -1) + "\t" + parts.head + "\n" + parts.tail.map(" " * maxLength + "\t" + _).mkString("\t", "\n\t", "")
                else
                    e._1.pad(maxLength, ' ', -1) + "\t" + e._2
        }
            .foreach(_.prettyPrintln(render))
    }

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 [[io.github.ppdzm.utils.universal.cli.Render]]
     * @param interval   休眠间隔
     */
    def waiting(mission: String, sum: Int, symbol: Char = '=', textRender: String, interval: Int = 1000): Unit = {
        (1 to sum).foreach(i => {
            this.printStage(mission, sum, i, symbol, textRender)
            Thread.sleep(interval)
        })
    }

    /**
     * 以字符颜色渲染的方式输出有限循环程序的当前执行进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param present    当前已执行次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 [[io.github.ppdzm.utils.universal.cli.Render]]
     */
    def printStage(mission: String, sum: Int, present: Int, symbol: Char = '=', textRender: String): Unit = {
        val backgroundColor =
            if (symbol.toString == " ")
                "0;42"
            else
                textRender
        val percentage = present * 100 / sum
        val bar = symbol.toString * percentage
        val blanks = " " * (100 - percentage)
        val padding = " " * (sum.toString.length - present.toString.length)
        val head = "[" + s"${if (mission.notNullAndEmpty) mission + ": " else ""}$bar>".rendering(backgroundColor) + s"$blanks($padding$present/$sum,${("  " + percentage).takeRight(3)}%)".rendering(textRender) + "]"
        val progress = present match {
            case 1 => s"${if (mission.notNullAndEmpty) s"${DateTimeUtils.nowWithFormat("yyyy-MM-dd HH:mm:ss")} INFO Start execute mission【${mission.rendering(textRender)}】\n" else ""}$head"
            case `sum` => s"$carriageReturn$head${if (mission.notNullAndEmpty) s"\n${DateTimeUtils.nowWithFormat("yyyy-MM-dd HH:mm:ss")} INFO Mission 【${mission.rendering(textRender)}】 accomplished" else ""}\n"
            case _ => s"$carriageReturn$head"
        }
        print(progress)
    }

}