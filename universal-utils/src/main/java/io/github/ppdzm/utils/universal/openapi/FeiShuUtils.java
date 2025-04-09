package io.github.ppdzm.utils.universal.openapi;

import io.github.ppdzm.utils.universal.http.HttpRequest;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 */
public class FeiShuUtils {
    public static void sendTextMessage(String url, String message) throws IOException {
        String format = "{\"msg_type\":\"text\",\"content\":{\"text\":\"%s\"}}";
        String content = String.format(format, StringEscapeUtils.escapeJson(message));
        new HttpRequest(url).header("Content-Type", "application/json").postData(content);
    }
}
