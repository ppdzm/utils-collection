package io.github.ppdzm.utils.universal.core

import java.lang.management.ManagementFactory
import java.lang.reflect.InvocationTargetException

import sun.management.VMManagement

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
object Program {
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
