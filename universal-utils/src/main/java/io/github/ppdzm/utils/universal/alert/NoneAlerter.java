package io.github.ppdzm.utils.universal.alert;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class NoneAlerter implements Alerter {
    @Override
    public void alert(String subject, String content, Exception e) throws Exception {
        if (e != null) {
            throw e;
        }
    }
}
