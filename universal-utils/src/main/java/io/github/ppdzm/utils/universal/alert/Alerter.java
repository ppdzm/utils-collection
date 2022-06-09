package io.github.ppdzm.utils.universal.alert;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public interface Alerter {
    /**
     * 告警器
     *
     * @param subject 主题
     * @param content 内容
     * @param e       异常
     * @throws Exception exception
     */
    void alert(String subject, String content, Exception e) throws Exception;
}
