package io.github.ppdzm.utils.universal.alert;


import io.github.ppdzm.utils.universal.base.Logging;
import io.github.ppdzm.utils.universal.base.StringUtils;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class LoggerAlerter extends Logging implements Alerter {
    private static final long serialVersionUID = 2174980471033189555L;

    @Override
    public void alert(String subject, String content, Exception e) {
        if (StringUtils.isNotNullAndEmpty(subject)) {
            logError(content, e);
        } else {
            logError("【" + subject + "】" + content, e);
        }
    }
}
