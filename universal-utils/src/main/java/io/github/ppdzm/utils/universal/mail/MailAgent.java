package io.github.ppdzm.utils.universal.mail;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2021/4/9.
 */
@AllArgsConstructor
@Builder
@Data
public class MailAgent {
    private String host;
    private int port;
    private String sender;
    private String username;
    private String password;

    private Properties getProperties() throws GeneralSecurityException {
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.socketFactory", sf);
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        return properties;
    }

    public void send(String subject, String content, String recipients, String ccs, List<File> attachments) throws GeneralSecurityException, MessagingException {
        Properties properties = getProperties();
        SMTPAuthenticator auth = new SMTPAuthenticator(username, password);
        Session session = Session.getDefaultInstance(properties, auth);
        MimeMessage message = new MimeMessage(session);
        //设置主题
        message.setSubject(subject);
        //设置发件人
        message.setFrom(new InternetAddress(sender));
        //设置收件人
        message.addRecipients(Message.RecipientType.TO, recipients);
        // 设置抄送
        if (ccs == null || ccs.isEmpty()) {
            message.addRecipients(Message.RecipientType.CC, ccs);
        }
        MimeMultipart multipart = new MimeMultipart();
        //添加正文
        MimeBodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(content, "text/plain;charset=utf-8");
        multipart.addBodyPart(contentPart);
        //添加附件
        if (attachments != null) {
            for (File attachment : attachments) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(attachment.getName());
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        //设置邮件内容
        message.setContent(multipart);
        //发送邮件
        Transport transport = session.getTransport();
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

}
