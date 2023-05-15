package io.github.ppdzm.utils.universal.formats.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.github.ppdzm.utils.universal.base.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author Created by Stuart Alex on 2023/5/12.
 */
public class FastJsonUtils {

    public static JSONObject mergeJsonObject(JSONObject prior, JSONObject secondary) {
        Set<String> keySet = secondary.keySet();
        for (String key : keySet) {
            JSONObject jsonObject = secondary.getJSONObject(key);
            prior.put(key, jsonObject);
        }
        return prior;
    }

    public static String readByPath(JSONObject jsonObject, String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return null;
        }
        try {
            Object read = JsonPath.read(jsonObject, path);
            if (read != null) {
                return read.toString();
            } else {
                return null;
            }
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    public static JSONObject readJsonFile(String path) throws IOException {
        final String fileContent = String.join("", FileUtils.readLines(new File(path), StandardCharsets.UTF_8));
        return JSON.parseObject(fileContent);
    }

}
