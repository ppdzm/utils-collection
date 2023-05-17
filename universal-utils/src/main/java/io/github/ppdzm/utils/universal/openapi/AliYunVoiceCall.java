package io.github.ppdzm.utils.universal.openapi;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 * 阿里云电话报警工具类
 */
public class AliYunVoiceCall {
    /**
     * 产品域名（接口地址固定，无需修改）
     */

    private final static String DOMAIN = "dyvmsapi.aliyuncs.com";
    /**
     * AK信息
     */
    private final static String ACCESS_KEY_ID = "LTAI5zqsai1mqpWdw";
    private final static String ACCESS_KEY_SECRET = "K2pqjuDvchBQhsal4MzNX9dqQhErfsr";
    /**
     * 云通信产品-语音API服务产品名称（产品名固定，无需修改）
     */
    private final static String PRODUCT = "Dyvmsapi";
    private final static String REGION_ID = "cn-hangzhou";
    private final static String ENDPOINT_NAME = "cn-hangzhou";
    private final static String SHOW_NUMBER = "02160881888";
    private final static String LEGAL_RESPONSE = "OK";

    /**
     * 电话报警
     *
     * @param showNumber   购买的显号
     * @param phoneNumbers 电话号码
     * @param ttsCode      tts code
     * @param ttsParam     tts param
     */
    public static void call(String showNumber, List<String> phoneNumbers, String ttsCode, String ttsParam) throws ClientException {
        for (String phoneNumber : phoneNumbers) {
            call(showNumber, phoneNumber, ttsCode, ttsParam);
        }
    }

    /**
     * 电话报警
     *
     * @param showNumber  购买的显号
     * @param phoneNumber 电话号码
     * @param ttsCode     tts code
     * @param ttsParam    tts param
     */
    public static void call(String showNumber, String phoneNumber, String ttsCode, String ttsParam) throws ClientException {

        //设置访问超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient 暂时不支持多region
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        DefaultProfile.addEndpoint(ENDPOINT_NAME, REGION_ID, PRODUCT, DOMAIN);
        DefaultAcsClient acsClient = new DefaultAcsClient(profile);

        SingleCallByTtsRequest request = new SingleCallByTtsRequest();
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        request.setCalledShowNumber(showNumber);
        //必填-被叫号码
        request.setCalledNumber(phoneNumber);
        //必填-Tts模板ID
        request.setTtsCode(ttsCode);
        //可选-当模板中存在变量时需要设置此值
        request.setTtsParam(ttsParam);
        //可选-音量 取值范围 0--200
        request.setVolume(100);
        //可选-播放次数
        request.setPlayTimes(3);
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        try {
            SingleCallByTtsResponse singleCallByTtsResponse = acsClient.getAcsResponse(request);

            System.out.println("singleCallByTtsResponse.getCode: " + singleCallByTtsResponse.getCode());
            if (singleCallByTtsResponse.getCode() != null && LEGAL_RESPONSE.equals(singleCallByTtsResponse.getCode())) {
                //请求成功
                System.out.println("语音文本外呼---------------");
                System.out.println("RequestId=" + singleCallByTtsResponse.getRequestId());
                System.out.println("Code=" + singleCallByTtsResponse.getCode());
                System.out.println("Message=" + singleCallByTtsResponse.getMessage());
                System.out.println("CallId=" + singleCallByTtsResponse.getCallId());
            }
        } catch (Exception e) {
            System.out.println("AliYunVoiceCall call failed. error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
