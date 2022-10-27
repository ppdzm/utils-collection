package io.github.ppdzm.utils.universal.alert;

import io.github.ppdzm.utils.universal.base.ExceptionUtils;
import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.openapi.WeChatWorkUtils;

/**
 * @author Created by Stuart Alex on 2022/4/27.
 */
public class WeChatWorkAlerter implements Alerter {
    private final String url;

    public WeChatWorkAlerter(String url) {
        this.url = url;
    }

    @Override
    public void alert(String subject, String content, Exception e) throws Exception {
        if (e != null) {
            if (StringUtils.isNullOrEmpty(subject)) {
                WeChatWorkUtils.sendTextMessage(this.url, String.format("%s\n\n%s", content, ExceptionUtils.exceptionToString(e)));
            } else {
                WeChatWorkUtils.sendTextMessage(this.url, String.format("【%s】\n%s\n\n%s", subject, content, ExceptionUtils.exceptionToString(e)));
            }
        } else {
            if (StringUtils.isNullOrEmpty(subject)) {
                WeChatWorkUtils.sendTextMessage(this.url, String.format("%s", content));
            } else {
                WeChatWorkUtils.sendTextMessage(this.url, String.format("【%s】\n%s", subject, content));
            }
        }
    }
}
