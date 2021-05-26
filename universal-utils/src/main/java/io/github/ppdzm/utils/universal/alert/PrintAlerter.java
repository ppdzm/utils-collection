package io.github.ppdzm.utils.universal.alert;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class PrintAlerter implements Alerter {
    @Override
    public void alert(String subject, String content) {
        System.out.println(subject);
        System.out.println(content);
    }
}
