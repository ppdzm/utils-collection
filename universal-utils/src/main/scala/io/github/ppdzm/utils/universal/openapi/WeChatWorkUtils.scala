package io.github.ppdzm.utils.universal.openapi

import org.apache.commons.lang3.StringEscapeUtils
import scalaj.http.{Http, HttpResponse}

object WeChatWorkUtils {

    def sendTextMessage(url: String, message: String): HttpResponse[Map[String, String]] = {
        val format =
            """{
              |    "msgtype": "text",
              |    "text": {
              |        "content": "%s"
              |    }
              |}""".stripMargin
        val content = format.format(StringEscapeUtils.escapeJson(message))
        Http(url).header("Content-Type", "application/json").postData(content).asParamMap
    }

}
