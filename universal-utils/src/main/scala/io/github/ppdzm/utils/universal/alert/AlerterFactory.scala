package io.github.ppdzm.utils.universal.alert

/**
 * Created by Stuart Alex on 2021/4/12.
 */
object AlerterFactory {

    def getAlerter(alertConfig: AlertConfig): Alerter = {
        import alertConfig._
        getAlerter(ALERTER_TYPE.stringValue, alertConfig)
    }

    def getAlerter(alerterType: String, alertConfig: AlertConfig): Alerter = {
        import alertConfig._
        alerterType.toLowerCase match {
            case "ding_talk" | "ding-talk" | "dingtalk" => new DingTalkAlerter(ALERTER_DING_TALK_WEB_HOOK_URL.stringValue, ALERTER_DING_TALK_RECEIVERS.arrayValue(), ALERTER_DING_TALK_RECEIVER_IS_AT_ALL.booleanValue)
            case "logger" => new LoggerAlerter()
            case "mail" => new MailAlerter(ALERTER_MAIL_SMTP_HOST.stringValue, ALERTER_MAIL_SMTP_PORT.intValue, ALERTER_MAIL_SENDER_USERNAME.stringValue, ALERTER_MAIL_SENDER_PASSWORD.stringValue, ALERTER_MAIL_SENDER_NAME.stringValue, ALERTER_MAIL_RECIPIENTS.arrayValue())
            case "multi" => new MultiAlerter(ALERTER_MULTI_TYPES.arrayValue().map(getAlerter(_, alertConfig)))
            case "none" => new NoneAlerter()
            case "print" => new PrintAlerter()
            case "wechat_work" | "wechat-work" | "wechatwork" => new WeChatWorkAlerter(ALERTER_WECHAT_WORK_WEB_HOOK_URL.stringValue)
            case _ => throw new UnsupportedAlerterException(alerterType)
        }
    }

}
