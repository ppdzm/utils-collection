package io.github.ppdzm.utils.universal.alert;

import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.openapi.DingTalkUtils;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 */
public class DingTalkAlerter implements Alerter {
    private final String url;
    private final String[] atMobiles;
    private final boolean isAtAll;

    public DingTalkAlerter(String url, String[] atMobiles) {
        this(url, atMobiles, false);
    }

    public DingTalkAlerter(String url, String[] atMobiles, boolean isAtAll) {
        this.url = url;
        this.atMobiles = atMobiles;
        this.isAtAll = isAtAll;
    }

    @Override
    public void alert(String subject, String content, Exception e) throws Exception {
        if (e != null) {
            if (StringUtils.isNotNullAndEmpty(subject)) {
                DingTalkUtils.sendTextMessage(url, content + "\n" + e.getMessage(), atMobiles, isAtAll);
            } else {
                DingTalkUtils.sendTextMessage(url, subject + "\n" + content + "\n" + e.getMessage(), atMobiles, isAtAll);
            }
        } else {
            if (StringUtils.isNotNullAndEmpty(subject)) {
                DingTalkUtils.sendTextMessage(url, content, atMobiles, isAtAll);
            } else {
                DingTalkUtils.sendTextMessage(url, subject + "\n" + content, atMobiles, isAtAll);
            }
        }
    }
}
