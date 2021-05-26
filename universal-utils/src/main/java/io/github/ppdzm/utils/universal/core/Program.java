package io.github.ppdzm.utils.universal.core;

import sun.management.VMManagement;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
public class Program {

    /**
     * 获取当前进程pid
     *
     * @return pid
     */
    public static int getProcessId() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Field jvm = null;
        try {
            jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            VMManagement management = (VMManagement) jvm.get(runtime);
            Method pidMethod = management.getClass().getDeclaredMethod("getProcessId");
            pidMethod.setAccessible(true);
            return (int) pidMethod.invoke(management);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
