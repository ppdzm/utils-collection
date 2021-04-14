package io.github.ppdzm.utils.universal.config

import java.nio.charset.StandardCharsets

import io.github.ppdzm.utils.universal.base.StringUtils
import org.sa.utils.universal.base.StringUtils
import org.sa.utils.universal.implicits.BasicConversions._

import scala.reflect.ClassTag

/**
 * Created by Stuart Alex on 2017/12/18.
 */
object ConfigItem extends Serializable {
    def apply(key: String)(implicit config: Config): ConfigItem = new ConfigItem(config, key, null)

    def apply(key: String, defaultValue: Any)(implicit config: Config): ConfigItem = new ConfigItem(config, key, defaultValue)
}

class ConfigItem private[config](config: Config, key: String, defaultValue: Any) extends Serializable {

    def booleanValue: Boolean = this.stringValue.toBoolean

    def bytesValue: Array[Byte] = this.stringValue.getBytes(StandardCharsets.UTF_8)

    def stringValue: String = {
        if (defaultValue.isNull)
            config.getProperty(key, null)
        else
            config.getProperty(key, defaultValue.toString)
    }

    /**
     * 调用rawValue时不会用到defaultValue
     *
     * @return
     */
    def specificValue[T: ClassTag]: T = config.get[T](key)

    def getKey: String = this.key

    def intValue: Int = this.stringValue.toInt

    def isDefined: Boolean = config.isDefined(key)

    def longValue: Long = this.stringValue.toLong

    def mapValue(fieldSeparator: String = ",", keyValueSeparator: String = ":"): Map[String, String] = {
        arrayValue(fieldSeparator)
            .map(StringUtils.split(_, keyValueSeparator))
            .filter(_.length == 2)
            .map(splits => splits(0) -> splits(1))
            .toMap
    }

    def mapListValue(fieldSeparator: String = ",", keyValueSeparator: String = ":", valueSeparator: String = "~"): Map[String, List[String]] = {
        if (valueSeparator == null)
            arrayValue(fieldSeparator)
                .map(StringUtils.split(_, keyValueSeparator))
                .filter(_.length == 2)
                .map(splits => splits(0) -> splits(1))
                .groupBy(_._1)
                .map { e => e._1 -> e._2.map(_._2).toList }
        else
            arrayValue(fieldSeparator)
                .map(StringUtils.split(_, keyValueSeparator))
                .filter(_.length == 2)
                .map(splits => splits(0) -> StringUtils.split(splits(1), valueSeparator).toList)
                .toMap
    }

    def arrayValue(separator: String = ","): Array[String] = {
        StringUtils.split(stringValue, separator).filter(_.nonEmpty)
    }

    def newValue(value: Any): Unit = {
        config.addProperty(key, value)
    }

    override def toString: String = s"ConfigItem(key=$key,defaultValue=$defaultValue,value=$stringValue)"

}