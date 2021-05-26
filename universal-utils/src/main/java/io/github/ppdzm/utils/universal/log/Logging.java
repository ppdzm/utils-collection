package io.github.ppdzm.utils.universal.log;

import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class Logging implements Serializable {
    private static final long serialVersionUID = 1102659729708837677L;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private boolean logging2StandardOut() {
        return SystemProperties.getLogging2Stdout();
    }

    protected void logDebug(String message, Render... renders) {
        String renderedMessage;
        if (renders.length == 0) {
            renderedMessage = CliUtils.rendering(message, Render.GREEN);
        } else {
            renderedMessage = CliUtils.rendering(message, renders);
        }
        if (logging2StandardOut()) {
            System.out.println(renderedMessage);
        } else {
            logger.debug(renderedMessage);
        }
    }

    public void logDebug(Object object, Render... renders) {
        logDebug(object.toString(), renders);
    }

    protected void logInfo(String message, Render... renders) {
        String renderedMessage;
        if (renders.length == 0) {
            renderedMessage = CliUtils.rendering(message, Render.GREEN);
        } else {
            renderedMessage = CliUtils.rendering(message, renders);
        }
        if (logging2StandardOut()) {
            System.out.println(renderedMessage);
        } else {
            logger.info(renderedMessage);
        }
    }

    public void logInfo(Object object, Render... renders) {
        logInfo(object.toString(), renders);
    }

    protected void logInfo(List<String> messages, Map<String, Render> messageRenders) {
        String renderedMessage = CliUtils.rendering(messages, messageRenders);
        if (logging2StandardOut()) {
            System.out.println(renderedMessage);
        } else {
            logger.info(renderedMessage);
        }
    }

    public void logWarning(String message, Render... renders) {
        String renderedMessage;
        if (renders.length == 0) {
            renderedMessage = CliUtils.rendering(message, Render.YELLOW);
        } else {
            renderedMessage = CliUtils.rendering(message, renders);
        }
        if (logging2StandardOut()) {
            System.out.println(renderedMessage);
        } else {
            logger.warn(renderedMessage);
        }
    }

    public void logWarning(Object object, Render... renders) {
        logWarning(object.toString(), renders);
    }

    protected void logError(String message, Render... renders) {
        String renderedMessage;
        if (renders.length == 0) {
            renderedMessage = CliUtils.rendering(message, Render.RED);
        } else {
            renderedMessage = CliUtils.rendering(message, renders);
        }
        if (logging2StandardOut()) {
            System.out.println(renderedMessage);
        } else {
            logger.error(renderedMessage);
        }
    }
}
