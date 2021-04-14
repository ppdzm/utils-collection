package io.github.ppdzm.utils.universal.base

import java.lang.management.ManagementFactory
import java.lang.reflect.InvocationTargetException

import Symbols._
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import sun.management.VMManagement

/**
 * Created by Stuart Alex on 2017/8/27.
 */
object SundryUtils {

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 [[Renders]]
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
     * @param textRender 文本渲染器 [[Renders]]
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

    def printlnWithTime(msg: String, startTime: Long): Unit = {
        val time = DateTimeUtils.nowWithFormat("yyyy-MM-dd HH:mm:ss")
        val takes = System.currentTimeMillis() - startTime
        if (takes > 500) {
            println(s"$time $msg takes: $takes ms")
        }
    }

    /**
     * 获取当前进程pid
     *
     * @return pid
     */
    def getProcessId: Int = {
        val runtime = ManagementFactory.getRuntimeMXBean
        try {
            val jvm = runtime.getClass.getDeclaredField("jvm")
            jvm.setAccessible(true)
            val management = jvm.get(runtime).asInstanceOf[VMManagement]
            val pidMethod = management.getClass.getDeclaredMethod("getProcessId")
            pidMethod.setAccessible(true)
            pidMethod.invoke(management).asInstanceOf[Integer].asInstanceOf[Int]
        } catch {
            case e@(_: NoSuchFieldException | _: NoSuchMethodException | _: IllegalAccessException | _: InvocationTargetException) =>
                e.printStackTrace()
                -1
        }
    }

}