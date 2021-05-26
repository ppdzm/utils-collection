package io.github.ppdzm.utils.universal.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Created by Stuart Alex on 2021/4/9.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class SMTPAuthenticator extends Authenticator {
    private String username;
    private String password;

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
