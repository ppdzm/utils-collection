package io.github.ppdzm.utils.universal.alert;


import io.github.ppdzm.utils.universal.base.ExceptionUtils;
import io.github.ppdzm.utils.universal.mail.MailAgent;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
public class MailAlerter implements Alerter {
    private String recipients;
    private String ccs;
    private MailAgent mailAgent;

    public MailAlerter(String host,
                       int port,
                       String username,
                       String password,
                       String sender,
                       String recipients,
                       String ccs) {
        this.recipients = recipients;
        this.ccs = ccs;
        this.mailAgent = new MailAgent(host, port, username, password, sender);
    }

    @Override
    public void alert(String subject, String content, Exception e) throws GeneralSecurityException, MessagingException {
        if (e != null) {
            mailAgent.send(subject, content + "\n\n" + ExceptionUtils.exceptionToString(e), recipients, ccs, null);
        } else {
            mailAgent.send(subject, content, recipients, ccs, null);
        }
    }
}
