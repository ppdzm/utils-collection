package io.github.ppdzm.utils.universal.formats.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Created by Stuart Alex on 2021/5/23.
 */
public class JsonUtils {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * 压缩JSON字符串
     *
     * @param json JSON字符串
     * @return String
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String compact(String json) throws JsonProcessingException {
        return serialize(parse(json));
    }

    /**
     * 将Java Class序列化为压缩格式JSON字符串
     *
     * @param object Object
     * @return String
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String serialize(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * 将Java Class序列化为压缩格式JSON字符串
     *
     * @param object Object
     * @return String
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String serialize(Object object, boolean pretty) throws JsonProcessingException {
        if (pretty) {
            return pretty(object);
        } else {
            return OBJECT_MAPPER.writeValueAsString(object);
        }
    }

    /**
     * 读取JSON String为JsonNode
     *
     * @param json JSON字符串
     * @return JsonNode
     * @throws JsonProcessingException JsonProcessingException
     */
    public static JsonNode parse(String json) throws JsonProcessingException {
        return parse(json, JsonNode.class);
    }

    /**
     * 读取JSON String为指定类型
     *
     * @param json       JSON字符串
     * @param returnType 返回类型
     * @param <T>        类型泛型
     * @return T
     * @throws JsonProcessingException JsonProcessingException
     */
    public static <T> T parse(String json, Class<T> returnType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, returnType);
    }

    /**
     * 将JSON字符串反序列化为Class
     *
     * @param json       JSON字符串
     * @param returnType 返回类型
     * @param <T>        类型泛型
     * @return T
     * @throws JsonProcessingException JsonProcessingException
     */
    public static <T> T deserialize(String json, Class<T> returnType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, returnType);
    }

    /**
     * 格式化JSON字符串
     *
     * @param json JSON字符串
     * @return String
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String pretty(String json) throws JsonProcessingException {
        return pretty(OBJECT_MAPPER.readTree(json));
    }

    /**
     * 将Case Class序列化为美化格式JSON字符串
     *
     * @param object Object
     * @return 格式化JSON字符串
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String pretty(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * 读取Json文件为JsonNode
     *
     * @param file Json文件
     * @return String
     * @throws IOException IOException
     */
    public static JsonNode parse(File file) throws IOException {
        return parse(file, JsonNode.class);
    }

    /**
     * 读取Json文件为JsonNode
     *
     * @param file       Json文件
     * @param returnType 返回类型
     * @param <T>        类型泛型
     * @return T
     * @throws IOException IOException
     */
    public static <T> T parse(File file, Class<T> returnType) throws IOException {
        return OBJECT_MAPPER.readValue(file, returnType);
    }

    /**
     * 读取Json InputStream为JsonNode
     *
     * @param inputStream Json InputStream
     * @return JsonNode
     * @throws IOException IOException
     */
    public static JsonNode parse(InputStream inputStream) throws IOException {
        return parse(inputStream, JsonNode.class);
    }

    /**
     * 读取Json InputStream为JsonNode
     *
     * @param inputStream Json InputStream
     * @param returnType  返回类型
     * @param <T>         T
     * @return T
     * @throws IOException IOException
     */
    public static <T> T parse(InputStream inputStream, Class<T> returnType) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, returnType);
    }
}
