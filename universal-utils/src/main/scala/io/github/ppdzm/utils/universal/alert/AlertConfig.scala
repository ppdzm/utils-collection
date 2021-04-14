package io.github.ppdzm.utils.universal.alert

import io.github.ppdzm.utils.universal.config.{Config, ConfigItem, ConfigTrait}
import org.sa.utils.universal.config.{Config, ConfigItem, ConfigTrait}

/**
 * Created by Stuart Alex on 2021/4/12.
 */
trait AlertConfig extends ConfigTrait {
    lazy val ALERTER_TYPE: ConfigItem = ConfigItem("alerter.type")
    lazy val ALERTER_DING_TALK_WEB_HOOK_URL: ConfigItem = ConfigItem("alerter.ding-talk.web-hook.url")
    lazy val ALERTER_DING_TALK_RECEIVERS: ConfigItem = ConfigItem("alerter.ding-talk.receivers")
    lazy val ALERTER_DING_TALK_RECEIVER_IS_AT_ALL: ConfigItem = ConfigItem("alerter.ding-talk.receiver.is-at-all")
    lazy val ALERTER_MAIL_SMTP_HOST: ConfigItem = ConfigItem("alerter.mail.smtp.host")
    lazy val ALERTER_MAIL_SMTP_PORT: ConfigItem = ConfigItem("alerter.mail.smtp.port")
    lazy val ALERTER_MAIL_SENDER_NAME: ConfigItem = ConfigItem("alerter.mail.sender.name")
    lazy val ALERTER_MAIL_SENDER_USERNAME: ConfigItem = ConfigItem("alerter.mail.sender.username")
    lazy val ALERTER_MAIL_SENDER_PASSWORD: ConfigItem = ConfigItem("alerter.mail.sender.password")
    lazy val ALERTER_MAIL_RECIPIENTS: ConfigItem = ConfigItem("alerter.mail.recipients")
    lazy val ALERTER_MULTI_TYPES: ConfigItem = ConfigItem("alerter.multi.types")
    lazy val ALERTER_WECHAT_WORK_WEB_HOOK_URL: ConfigItem = ConfigItem("alerter.wechat-work.web-hook.url")
}

object AlertConfig {
    def apply(implicit config: Config): AlertConfig = new AlertConfig {
        override implicit protected val config: Config = config
    }
}
