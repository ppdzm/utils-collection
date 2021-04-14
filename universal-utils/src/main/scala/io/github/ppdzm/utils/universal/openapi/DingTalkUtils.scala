package io.github.ppdzm.utils.universal.openapi

import org.apache.commons.lang3.StringEscapeUtils
import scalaj.http.{Http, HttpResponse}

/**
 * @author StuartAlex on 2019-05-09 19:40
 */
object DingTalkUtils {

    def sendTextMessage(url: String,
                        message: String,
                        atMobiles: Array[String],
                        isAtAll: Boolean = false): HttpResponse[Map[String, String]] = {
        val format =
            """{
              |    "msgtype": "text",
              |    "text": {
              |        "content": "%s"
              |    },
              |    "at": {
              |        "atMobiles": [%s],
              |        "isAtAll": %s
              |    }
              |}""".stripMargin
        val content = format.format(StringEscapeUtils.escapeJson(message), atMobiles.map(m => s""""$m"""").mkString(","), isAtAll)
        Http(url).header("Content-Type", "application/json").postData(content).asParamMap
    }

}