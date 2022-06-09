package io.github.ppdzm.utils.universal.alert;


import io.github.ppdzm.utils.universal.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class AlerterFactory {
    public static Alerter getAlerter(Config config) throws Exception {
        return getAlerter(new AlertConfig(config));
    }

    public static Alerter getAlerter(AlertConfig alertConfig) throws Exception {
        return getAlerter(alertConfig.ALERTER_TYPE.stringValue(), alertConfig);
    }

    public static Alerter getAlerter(String alerterType, AlertConfig alertConfig) throws Exception {
        switch (alerterType.toLowerCase()) {
            case "ding-talk":
                return new DingTalkAlerter(
                    alertConfig.ALERTER_DING_TALK_WEB_HOOK_URL.stringValue(),
                    alertConfig.ALERTER_DING_TALK_RECEIVERS.arrayValue(),
                    alertConfig.ALERTER_DING_TALK_RECEIVER_IS_AT_ALL.booleanValue()
                );
            case "logger":
                return new LoggerAlerter();
            case "mail":
                return new MailAlerter(
                    alertConfig.ALERTER_MAIL_SMTP_HOST.stringValue(),
                    alertConfig.ALERTER_MAIL_SMTP_PORT.intValue(),
                    alertConfig.ALERTER_MAIL_SENDER_USERNAME.stringValue(),
                    alertConfig.ALERTER_MAIL_SENDER_PASSWORD.stringValue(),
                    alertConfig.ALERTER_MAIL_SENDER_NAME.stringValue(),
                    alertConfig.ALERTER_MAIL_RECIPIENTS.stringValue(),
                    alertConfig.ALERTER_MAIL_CCS.stringValue()
                );
            case "multi":
                String[] alerterTypes = alertConfig.ALERTER_MULTI_TYPES.arrayValue();
                List<Alerter> alerterArray = new ArrayList<>();
                for (String type : alerterTypes) {
                    alerterArray.add(getAlerter(type, alertConfig));
                }
                return new MultiAlerter(alerterArray);
            case "none":
                return new NoneAlerter();
            case "print":
                return new PrintAlerter();
            case "wechat-work":
                return new WeChatWorkAlerter(alertConfig.ALERTER_WECHAT_WORK_WEB_HOOK_URL.stringValue());
            default:
                return getCustomizedAlerter(alerterType, alertConfig);
        }
    }

    public static Alerter getCustomizedAlerter(String alerterType, AlertConfig alertConfig) throws UnsupportedAlerterException {
        throw new UnsupportedAlerterException(alerterType);
    }

}
