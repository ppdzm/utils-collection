package io.github.ppdzm.utils.universal.base

import org.scalatest.FunSuite

class MailAgentScalaTest extends FunSuite {

    test("sendEmail") {
        val content =
            """This is a test e-mail from moon official, please do not reply, thanks for your cooperation.
              |这是一封来自moon官方的测试邮件，请勿回复，谢谢您的合作。""".stripMargin
        println("before send mail")
        //        MailAgent.send("Test e-mail from moon", content)
        println("after send mail")
    }

}