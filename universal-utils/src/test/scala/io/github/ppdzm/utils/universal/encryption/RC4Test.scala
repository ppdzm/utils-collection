package io.github.ppdzm.utils.universal.encryption

import cn.hutool.core.util.StrUtil
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.scalatest.FunSuite

import java.nio.charset.StandardCharsets

/**
 * Created by Stuart Alex on 2021/4/9.
 */
class RC4Test extends FunSuite {

    test("rc4") {

        val log =
            """{
              |	"@timestamp": "2019-01-17T09:03:19.984Z",
              |	"@metadata": {
              |		"beat": "filebeat",
              |		"type": "doc",
              |		"version": "6.1.1",
              |		"topic": "systemacclogs"
              |	},
              |	"json": {},
              |	"k8s_node_name": "ap-southeast-1.i-t4nblt7ggik8kjmrgoo3",
              |	"type": "systemacclogs",
              |	"prospector": {
              |		"type": "log"
              |	},
              |	"source": "/host/var/lib/docker/containers/54588189d20a3b5e7b66cd61bbd7fd1658b0b689fa1eb55f0e09d39a3839ea5c/54588189d20a3b5e7b66cd61bbd7fd1658b0b689fa1eb55f0e09d39a3839ea5c-json.log",
              |	"message": "{\"host\":\"enervated-giraffe-lala-go-gateway-system-overseas-6cf9689d8cxbgs\",\"time\":1547715799.984,\"time2\":\"17/Jan/2019:17:03:19 +0800\",\"ip\":\"100.117.244.141\",\"ip2\":\"27.252.193.164\",\"scheme\":\"http\",\"method\":\"POST\",\"domain\":\"api.see-ly.com\",\"uri\":\"/v1/recommend/index\",\"reqlen\":731,\"upstream_addr\":\"127.0.0.1:8270\",\"upstream_status\":\"200\",\"ut\":\"0.264\",\"ut2\":0.264,\"replen\":3487,\"status\":200,\"agent\":\"okhttp/3.10.0\",\"referer\":\"-\",\"getps\":\"-\",\"postps\":\"\\xFF\\x05S\\x9D\\xD3V\\x13}\\x94\\xB7O\\x83}\\xBD\\xDE\\x179\\xE3\\x8F}\\xEE\\x9C\\xF8\\xA9j\\x09\\xE3\\xEF0\\x87\\x7F:;\\xB8\\xAE\\xDF\\xCC\\x9BF\\x00d\\xD2\\xC5\\xE2\\xCDB_;h\\x81\\x96UXS\\x80\\xF2z+\\x13\\xE0\\x05\\x11*\\xA4H\\x8Ch\\xC4u\\xAECG\\x80Svi/\\xCA[\\x09!\\x7FG;\\x9D\\xD1\\x5Cg\\x96AX\\x22/\\x8D)\\xCF!\\x82\\xE4nt\\x0E\\xA0\\xBEb0={:\\xD0\\x03b/\\x9F\\x8F\\xA5\\xCD\\x85\\xC6\\x97\\x00\\x95\\xCE\\x1Db~\\x02\\xEBs\\x1E\\x81(\\xA0R\\xE4\\x14W\\xDA\\x92z\\xD4q\\x89\\x8B\\x9D\\x87a6\\x9F\\x91\\x08a\\xA1\\xB9\\x14\u0026\\xB6\\x8E\\x99\\xF8u\\x8F\\x03\\xC92\\x1F\\xCA\\xF6S\\xECMP\\x92\\xA5\\x09\\xFB\\x85U\\xDD\\xF6\\x15\\x88_\\x0E\\xF8\\xB7a\\xC8\\xD5qd\\xAF\\x9B!'A\\xCCI\\x9E\\xFB\\xE3:;\\xEB\\xB7G\\x22\\x84\\xD3\\xB8\\x841\\x11=Is6_\\xD8-n\\xBE\\xE4\\xFA\\x9E\\xA0\\x09})3\\x91_\\x09\\x81y\\xFC\\xE3\\xB2\\x9BP\\xAD\\xAFCD\\xF0\\x02#\\x81\\xDC\\xA1\\x1FU\\xAFnk1\\xE6\\xAF\\xCA\\x02\\x0EM\\x171\\x18\\xE0\\x05\\x16\\xED\\xD9\\x0B\\x8F\\xF4\\xE3\\xA3\\xB43\\xCAx\u003e\\xBD,\\xA0N\\xBC\\x92Sy\\x93J[\\x9B\\xF5\\xF7\\x00\\xD9\\x95\\x9A;\\x15od\\x9D\\xDEk \\xF9\\xD0\\xE9Um=\\x87)\\xFC\\x81HF\u003c\\xDF\\xEC_\\xFArvz\\x09f\\x85h/\\x9E\\xA3;P\\xC4\\xDE\\x02\\xD9\\xD1\\xFD\\xBC\\xE9S\\x10\\xDB\\x87\\xDA\\x9D~\\x80\\x08\\xCA\\x19\\x0C\u003c\\xFA\\xFE\\xCEo$\\x04K\\x9F\\x82{1\\x18:Wb\\xFC\\xC4\\x15\\xB2\\xDF\\xA6P\\x17\\xA3t{\\xA8f\\xFCtR\\x9A!'\\xEA\\xC8\\xDAs:\\x99k\\xD5\\xE7\\xF4\\xA9\\x1E\\xDF\\x99\\xF6Z\\x14\\xC1k\\xE8\\xA9nzt2\\xF0\\xA8h\\xC6\\xBCv \\xA1%\\x18\\xDA\\x1A\\xA0z\\x93\\xC8\\xFB\\x02\\x123\\xA0X\\xCB\\xD8\\xB6\\x17\\x84\\xEB\\xA4\\x8Ft\\xC9\\xF0\\x01h$d\\x12r\\x17\",\"content_type\":\"application/octet-stream\"}",
              |	"k8s_container_name": "lala-go-gateway-system-overseas",
              |	"topic": "systemacclogs",
              |	"stream": "stdout",
              |	"docker_container": "k8s_lala-go-gateway-system-overseas_enervated-giraffe-lala-go-gateway-system-overseas-6cf9689d8cxbgs_lala-go_6e028337-1a35-11e9-be5a-00163e0214a8_0",
              |	"beat": {
              |		"hostname": "log-pilot-wqmls",
              |		"version": "6.1.1",
              |		"name": "log-pilot-wqmls"
              |	},
              |	"offset": 170430,
              |	"k8s_pod": "enervated-giraffe-lala-go-gateway-system-overseas-6cf9689d8cxbgs",
              |	"index": "systemacclogs",
              |	"k8s_pod_namespace": "lala-go"
              |}""".stripMargin
        val logJson = JsonUtils.parse(log)
        val message = logJson.get("message").asText()
        println(message)
        val unicodeEscapedMessage = message.escapeUnicode8
        println(unicodeEscapedMessage)
        val unicodeEscapedMessageJson = JsonUtils.parse(unicodeEscapedMessage)
        val postParameters = unicodeEscapedMessageJson.get("postps").asText().unescapeUnicode8
        println(postParameters)
        val dec = new cn.hutool.crypto.symmetric.RC4("R6qGg5^Pdv%4").crypt(postParameters.unicode82Bytes)
        val decryptedBytes = RC4.decrypt(postParameters.unicode82Bytes, "R6qGg5^Pdv%4")
        val decompressed = GZipUtils.decompress(decryptedBytes)
        val decom = GZipUtils.decompress(dec)
        println(JsonUtils.pretty(decompressed))
        println(decom)
    }

    test("aaa") {
        val key = "jetaime"
        val value = "gegkersg"
        println(StrUtil.str(RC4.encrypt(value.getBytes, key), "utf8"))
        println(StrUtil.str(new cn.hutool.crypto.symmetric.RC4(key).encrypt(value), "utf8"))
    }

}
