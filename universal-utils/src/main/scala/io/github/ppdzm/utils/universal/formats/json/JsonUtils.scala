package io.github.ppdzm.utils.universal.formats.json

import java.io.{File, InputStream}

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import org.json4s._
import org.json4s.native.Serialization

import scala.reflect.Manifest

/**
 * Created by Stuart Alex on 2017/8/21.
 */
object JsonUtils {
    private implicit val formats: Formats = Serialization.formats(NoTypeHints)
    private val objectMapper = new ObjectMapper()
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    /**
     * 压缩JSON字符串
     *
     * @param json JSON字符串
     * @return
     */
    def compact(json: String): String = serialize(parse(json))

    /**
     * 将JSON字符串反序列化为Class
     *
     * @param json JSON字符串
     * @return
     */
    def deserialize[T](json: String, clazz: Class[T]): T = objectMapper.readValue(json, clazz)

    /**
     * 格式化JSON字符串
     *
     * @param json JSON字符串
     * @return
     */
    def pretty(json: String): String = pretty(objectMapper.readTree(json))

    /**
     * 将Case Class序列化为美化格式JSON字符串
     *
     * @param anyRef AnyRef
     * @return
     */
    def pretty(anyRef: AnyRef): String = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(anyRef)

    /**
     * 将Java Class序列化为压缩格式JSON字符串
     *
     * @param anyRef AnyRef
     * @return
     */
    def serialize(anyRef: AnyRef, pretty: Boolean = false): String = {
        if (pretty)
            this.pretty(anyRef)
        else
            objectMapper.writeValueAsString(anyRef)
    }

    /**
     * 读取JSON String为JsonNode
     *
     * @param json JSON字符串
     * @return
     */
    def parse(json: String): JsonNode = parse(json, classOf[JsonNode])

    /**
     * 读取JSON String为指定类型
     *
     * @param json       JSON字符串
     * @param returnType 返回类型
     * @tparam T 类型泛型
     * @return
     */
    def parse[T](json: String, returnType: Class[T]): T = objectMapper.readValue(json, returnType)

    /**
     * 读取Json文件为JsonNode
     *
     * @param file Json文件
     * @return
     */
    def parse(file: File): JsonNode = parse(file, classOf[JsonNode])

    /**
     * 读取Json文件为JsonNode
     *
     * @param file       Json文件
     * @param returnType 返回类型
     * @tparam T 类型泛型
     * @return
     */
    def parse[T](file: File, returnType: Class[T]): T = objectMapper.readValue(file, returnType)

    /**
     * 读取Json InputStream为JsonNode
     *
     * @param inputStream Json InputStream
     * @return
     */
    def parse(inputStream: InputStream): JsonNode = parse(inputStream, classOf[JsonNode])

    /**
     * 读取Json InputStream为JsonNode
     *
     * @param inputStream Json InputStream
     * @param returnType  返回类型
     * @tparam T 类型泛型
     * @return
     */
    def parse[T](inputStream: InputStream, returnType: Class[T]): T = objectMapper.readValue(inputStream, returnType)

    /**
     * 将JSON字符串反序列化为Scala Case Class/Scala Class
     *
     * @param json JSON字符串
     * @return
     */
    def deserialize4s[T](json: String)(implicit mf: Manifest[T]): T = Serialization.read[T](json)

    /**
     * 将Case Class序列化为美化格式JSON字符串
     *
     * @param anyRef AnyRef
     * @return
     */
    def pretty4s(anyRef: AnyRef): String = Serialization.writePretty(anyRef)

    /**
     * 将Scala Class/Scala Case Class序列化为压缩格式JSON字符串
     *
     * @param anyRef AnyRef
     * @return
     */
    def serialize4s(anyRef: AnyRef, pretty: Boolean = false): String = {
        if (pretty)
            this.pretty4s(anyRef)
        else
            Serialization.write(anyRef)
    }

}
