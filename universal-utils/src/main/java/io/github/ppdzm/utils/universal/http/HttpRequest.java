package io.github.ppdzm.utils.universal.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/6/11.
 */
public class HttpRequest {
    private final String url;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse postData(String data) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        for (String key : headers.keySet()) {
            httpPost.setHeader(key, headers.get(key));
        }
        httpPost.setEntity(new StringEntity(data));
        return httpClient.execute(httpPost);
    }

}
