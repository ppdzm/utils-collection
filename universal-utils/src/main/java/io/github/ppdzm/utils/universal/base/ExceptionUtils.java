package io.github.ppdzm.utils.universal.base;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
public class ExceptionUtils {

    public static String exceptionToString(Throwable t) {
        StringBuilder message = new StringBuilder();
        message.append(t.toString());
        for (StackTraceElement element : t.getStackTrace()) {
            message.append("\n\tat ").append(element.toString());
        }
        message.append("\n");
        for (Throwable throwable : t.getSuppressed()) {
            message.append(exceptionToString(throwable));
        }
        message.append("\n");
        Throwable cause = t.getCause();
        if (cause != null) {
            message.append(exceptionToString(cause));
        }
        return message.toString();
//        final StringWriter sw = new StringWriter();
//        final PrintWriter pw = new PrintWriter(sw, true);
//        t.printStackTrace(pw);
//        return sw.getBuffer().toString();
    }

}
