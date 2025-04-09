package io.github.ppdzm.utils.universal.alert;

import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class AlertConfig {
    public ConfigItem ALERTER_TYPE;
    public ConfigItem ALERTER_DING_TALK_WEB_HOOK_URL;
    public ConfigItem ALERTER_DING_TALK_RECEIVERS;
    public ConfigItem ALERTER_DING_TALK_RECEIVER_IS_AT_ALL;
    public ConfigItem ALERTER_FEI_SHU_WEB_HOOK_URL;
    public ConfigItem ALERTER_MAIL_SMTP_HOST;
    public ConfigItem ALERTER_MAIL_SMTP_PORT;
    public ConfigItem ALERTER_MAIL_SENDER_NAME;
    public ConfigItem ALERTER_MAIL_SENDER_USERNAME;
    public ConfigItem ALERTER_MAIL_SENDER_PASSWORD;
    public ConfigItem ALERTER_MAIL_RECIPIENTS;
    public ConfigItem ALERTER_MAIL_CCS;
    public ConfigItem ALERTER_MULTI_TYPES;
    public ConfigItem ALERTER_WECHAT_WORK_WEB_HOOK_URL;

    public AlertConfig(Config config) {
        this.ALERTER_TYPE = new ConfigItem(config, "alerter.type", "none");
        this.ALERTER_DING_TALK_WEB_HOOK_URL = new ConfigItem(config, "alerter.ding-talk.web-hook.url");
        this.ALERTER_DING_TALK_RECEIVERS = new ConfigItem(config, "alerter.ding-talk.receivers");
        this.ALERTER_DING_TALK_RECEIVER_IS_AT_ALL = new ConfigItem(config, "alerter.ding-talk.receiver.is-at-all");
        this.ALERTER_FEI_SHU_WEB_HOOK_URL = new ConfigItem(config, "alerter.fei-shu.web-hook.url");
        this.ALERTER_MAIL_SMTP_HOST = new ConfigItem(config, "alerter.mail.smtp.host");
        this.ALERTER_MAIL_SMTP_PORT = new ConfigItem(config, "alerter.mail.smtp.port");
        this.ALERTER_MAIL_SENDER_NAME = new ConfigItem(config, "alerter.mail.sender.name");
        this.ALERTER_MAIL_SENDER_USERNAME = new ConfigItem(config, "alerter.mail.sender.username");
        this.ALERTER_MAIL_SENDER_PASSWORD = new ConfigItem(config, "alerter.mail.sender.password");
        this.ALERTER_MAIL_RECIPIENTS = new ConfigItem(config, "alerter.mail.recipients");
        this.ALERTER_MAIL_CCS = new ConfigItem(config, "alerter.mail.ccs");
        this.ALERTER_MULTI_TYPES = new ConfigItem(config, "alerter.multi.types");
        this.ALERTER_WECHAT_WORK_WEB_HOOK_URL = new ConfigItem(config, "alerter.wechat-work.web-hook.url");
    }
}
