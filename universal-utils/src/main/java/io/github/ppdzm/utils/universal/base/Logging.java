package io.github.ppdzm.utils.universal.base;

import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class Logging implements Serializable {
    private static final long serialVersionUID = 1102659729708837677L;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public Logger logger = LoggerFactory.getLogger(getClass());

    public static void setLogging2Stdout(boolean enabled) {
        SystemProperties.setLogging2Stdout(enabled);
    }

    public static String rendering(String message, Render... renders) {
        if (renders.length == 0) {
            return CliUtils.rendering(message, Render.MAGENTA);
        } else {
            return CliUtils.rendering(message, renders);
        }
    }

    protected void logDebug(String message, Render... renders) {
        String renderedMessage = rendering(message, renders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            logger.debug(renderedMessage);
        }
    }

    public void logDebug(Object object, Render... renders) {
        logDebug(object.toString(), renders);
    }

    protected void logInfo(String message, Render... renders) {
        String renderedMessage = rendering(message, renders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            logger.info(renderedMessage);
        }
    }

    public void logInfo(Object object, Render... renders) {
        logInfo(object.toString(), renders);
    }

    protected void logInfo(List<String> messages, Map<String, Render> messageRenders) {
        String renderedMessage = CliUtils.rendering(messages, messageRenders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            logger.info(renderedMessage);
        }
    }

    public void logWarning(String message, Render... renders) {
        String renderedMessage = rendering(message, renders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            logger.warn(renderedMessage);
        }
    }

    public void logWarning(Object object, Render... renders) {
        logWarning(object.toString(), renders);
    }

    protected void logError(String message, Render... renders) {
        String renderedMessage = rendering(message, renders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            logger.error(renderedMessage);
        }
    }

    protected void logError(String message, Exception e, Render... renders) {
        String renderedMessage = rendering(message, renders);
        if (SystemProperties.getLogging2Stdout()) {
            System.out.println(CliUtils.rendering(sdf.format(new Date()), Render.CYAN) + " " + renderedMessage);
        } else {
            if (e != null) {
                logger.error(renderedMessage, e);
            } else {
                logger.error(renderedMessage);
            }
        }
    }
}
