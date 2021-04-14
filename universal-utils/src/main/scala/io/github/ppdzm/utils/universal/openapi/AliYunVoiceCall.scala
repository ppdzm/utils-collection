package io.github.ppdzm.utils.universal.openapi

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest
import com.aliyuncs.profile.DefaultProfile

/**
 * 阿里云电话报警工具类
 */
object AliYunVoiceCall {
    //产品域名（接口地址固定，无需修改）
    val domain = "dyvmsapi.aliyuncs.com"
    //AK信息
    val accessKeyId = "LTAI5zqsai1mqpWdw"
    val accessKeySecret = "K2pqjuDvchBQhsal4MzNX9dqQhErfsr"
    //云通信产品-语音API服务产品名称（产品名固定，无需修改）
    private val product = "Dyvmsapi"
    private val regionId = "cn-hangzhou"
    private val endpointName = "cn-hangzhou"
    private val showNumber = "02160881888"

    def call(phoneNumbers: List[String], ttsCode: String, ttsParam: String): Unit = {
        phoneNumbers.foreach {
            phoneNumber => call(phoneNumber, ttsCode, ttsParam)
        }
    }

    /**
     * 电话报警
     *
     * @param phoneNumber 电话号码
     * @param ttsCode     tts code
     * @param ttsParam    tts param
     */
    def call(phoneNumber: String, ttsCode: String, ttsParam: String): Unit = {

        //设置访问超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000")
        System.setProperty("sun.net.client.defaultReadTimeout", "10000")


        //初始化acsClient 暂时不支持多region
        val profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret)
        DefaultProfile.addEndpoint(endpointName, regionId, product, domain)
        val acsClient = new DefaultAcsClient(profile)

        val request = new SingleCallByTtsRequest
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        request.setCalledShowNumber(showNumber)
        //必填-被叫号码
        request.setCalledNumber(phoneNumber)
        //必填-Tts模板ID
        request.setTtsCode(ttsCode)
        //可选-当模板中存在变量时需要设置此值
        request.setTtsParam(ttsParam)
        //可选-音量 取值范围 0--200
        request.setVolume(100)
        //可选-播放次数
        request.setPlayTimes(3)
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
        request.setOutId("yourOutId")

        //hint 此处可能会抛出异常，注意catch
        try {
            val singleCallByTtsResponse = acsClient.getAcsResponse(request)

            println(s"singleCallByTtsResponse.getCode: ${singleCallByTtsResponse.getCode}")
            if (singleCallByTtsResponse.getCode != null && singleCallByTtsResponse.getCode == "OK") { //请求成功
                println("语音文本外呼---------------")
                println("RequestId=" + singleCallByTtsResponse.getRequestId)
                println("Code=" + singleCallByTtsResponse.getCode)
                println("Message=" + singleCallByTtsResponse.getMessage)
                println("CallId=" + singleCallByTtsResponse.getCallId)
            }
        } catch {
            case e: Exception =>
                println(s"AliYunVoiceCall call failed. error: $e")
                e.printStackTrace()
        }

    }

}
