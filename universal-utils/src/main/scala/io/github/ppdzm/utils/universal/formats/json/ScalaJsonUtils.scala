package io.github.ppdzm.utils.universal.formats.json

import org.json4s._
import org.json4s.native.Serialization

import scala.reflect.Manifest

/**
 * Created by Stuart Alex on 2017/8/21.
 */
object ScalaJsonUtils {
    private implicit val formats: Formats = Serialization.formats(NoTypeHints)

    /**
     * 将JSON字符串反序列化为Scala Case Class/Scala Class
     *
     * @param json JSON字符串
     * @return
     */
    def deserialize4s[T](json: String)(implicit mf: Manifest[T]): T = Serialization.read[T](json)

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

    /**
     * 将Case Class序列化为美化格式JSON字符串
     *
     * @param anyRef AnyRef
     * @return
     */
    def pretty4s(anyRef: AnyRef): String = Serialization.writePretty(anyRef)

}
