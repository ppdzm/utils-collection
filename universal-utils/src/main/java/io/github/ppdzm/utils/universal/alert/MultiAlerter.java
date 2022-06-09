package io.github.ppdzm.utils.universal.alert;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@AllArgsConstructor
public class MultiAlerter implements Alerter {
    private final List<Alerter> alerterList;

    @Override
    public void alert(String subject, String content, Exception e) throws Exception {
        for (Alerter alerter : alerterList) {
            alerter.alert(subject, content, e);
        }
    }
}
