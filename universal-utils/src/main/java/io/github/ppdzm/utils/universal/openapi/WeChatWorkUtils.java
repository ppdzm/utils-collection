package io.github.ppdzm.utils.universal.openapi;

import io.github.ppdzm.utils.universal.http.HttpRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 */
public class WeChatWorkUtils {
    public static CloseableHttpResponse sendTextMessage(String url, String message) throws IOException {
        String format = "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
        String content = String.format(format, StringEscapeUtils.escapeJson(message));
        return new HttpRequest(url).header("Content-Type", "application/json").postData(content);
    }
}
