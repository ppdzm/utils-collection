package io.github.ppdzm.utils.universal.alert;


import io.github.ppdzm.utils.universal.log.Logging;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class LoggerAlerter extends Logging implements Alerter {
    private static final long serialVersionUID = 2174980471033189555L;

    @Override
    public void alert(String subject, String content) {
        logError("【" + subject + "】");
        logError(content);
    }
}
