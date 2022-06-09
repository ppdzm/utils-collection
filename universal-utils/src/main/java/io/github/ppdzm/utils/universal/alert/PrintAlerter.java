package io.github.ppdzm.utils.universal.alert;

import io.github.ppdzm.utils.universal.base.StringUtils;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class PrintAlerter implements Alerter {
    @Override
    public void alert(String subject, String content, Exception e) {
        if (!StringUtils.isNotNullAndEmpty(subject)) {
            System.out.println(subject);
        }
        System.out.println(content);
        if (e != null) {
            e.printStackTrace();
        }
    }
}
