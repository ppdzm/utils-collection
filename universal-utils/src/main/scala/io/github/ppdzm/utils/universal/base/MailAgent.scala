package io.github.ppdzm.utils.universal.base

import java.io.File
import java.util.Properties

import com.sun.mail.util.MailSSLSocketFactory
import javax.activation.{DataHandler, FileDataSource}
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import javax.mail.{Authenticator, Message, PasswordAuthentication, Session}

/**
 * @author StuartAlex on 2019-07-25 16:22
 */
class MailAgent(host: String,
                port: Int,
                username: String,
                password: String,
                sender: String,
                defaultRecipients: Array[String] = Array[String]()) {
    private val sf = new MailSSLSocketFactory()
    sf.setTrustAllHosts(true)
    private val props = new Properties()
    props.put("mail.smtp.ssl.socketFactory", sf)
    props.put("mail.smtp.ssl.trust", "*")
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.port", port.toString)
    props.put("mail.smtp.ssl.enable", "true")
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.auth", "true")

    def send(subject: String, content: String): Unit = {
        assert(defaultRecipients.nonEmpty, "default recipients is empty")
        send(subject, content, defaultRecipients)
    }

    def send(subject: String, content: String, recipients: Array[String], ccs: Array[String] = Array(), attachments: List[File] = List[File]()): Unit = {
        try {
            val newContent = content.split("\n").map {
                line =>
                    if (!line.startsWith("<div>"))
                        String.format("<div>%s</div>", line)
                    else
                        line
            }.mkString("\n")
            val auth = new SMTPAuthenticator(username, password)
            val session = Session.getDefaultInstance(props, auth)
            val message = new MimeMessage(session)
            //设置主题
            message.setSubject(subject)
            //设置发件人
            message.setFrom(new InternetAddress(sender))
            //设置收件人
            message.addRecipients(Message.RecipientType.TO, recipients.mkString(","))
            // 设置抄送
            if (ccs.nonEmpty)
                message.addRecipients(Message.RecipientType.CC, ccs.mkString(","))
            val multipart = new MimeMultipart()
            //添加正文
            val contentPart = new MimeBodyPart()
            contentPart.setContent(newContent, "text/html;charset=utf-8")
            multipart.addBodyPart(contentPart)
            //添加附件
            for (f <- attachments) {
                val attachmentBodyPart = new MimeBodyPart()
                val source = new FileDataSource(f)
                attachmentBodyPart.setDataHandler(new DataHandler(source))
                attachmentBodyPart.setFileName(f.getName)
                multipart.addBodyPart(attachmentBodyPart)
            }
            //设置邮件内容
            message.setContent(multipart)
            //发送邮件
            val transport = session.getTransport
            transport.connect()
            transport.sendMessage(message, message.getAllRecipients)
            transport.close()
        } catch {
            case e: Exception => println("send mail failed. error: " + e.toString)
        }
    }

    private class SMTPAuthenticator(username: String, password: String) extends Authenticator {
        override def getPasswordAuthentication = new PasswordAuthentication(username, password)
    }

}
