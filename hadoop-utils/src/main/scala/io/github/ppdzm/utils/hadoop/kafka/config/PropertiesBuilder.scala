package io.github.ppdzm.utils.hadoop.kafka.config

import _root_.io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.kafka.common.config.AbstractConfig

import java.util
import java.util.Properties
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.reflect._

/**
 * Created by Stuart Alex on 2021/1/28.
 */
private[config] abstract class PropertiesBuilder[T <: AbstractConfig : ClassTag] {
    private lazy val instance = null.asInstanceOf[T]
    // AbstractConfig子类的所有已CONFIG结尾的静态字段
    private lazy val fields = classTag[T].runtimeClass.getFields.filter(_.getName.endsWith("CONFIG"))
    // Builder类的方法
    private lazy val builderMethods = getClass.getMethods
    protected val properties = new Properties()
    private val logging = new Logging(getClass)

    def build(): Properties = properties

    def put(key: String, value: AnyRef): this.type = {
        properties.put(key, value)
        this
    }

    def invoke(parameters: Map[String, AnyRef]): this.type = {
        if (parameters != null)
            invoke(parameters.asJava)
        this
    }

    def invoke(parameters: util.Map[String, AnyRef]): this.type = {
        if (parameters == null)
            return this
        parameters.foreach {
            case (key, value) =>
                // 通过配置名称找到字段
                val fieldOption = fields.find(_.get(instance) == key)
                if (fieldOption.isDefined) {
                    this.logging.logInfo(s"try find method matches config field ${fieldOption.get.getName.red} ${"with value".green} ${key.red}")
                    // 通过字段名称找方法
                    invoke(fieldOption.get.getName.trimEnd("_CONFIG"), value)
                }
                else {
                    this.logging.logWarning(s"config filed with value ${key.red} ${"not found".yellow}")
                }
        }
        this
    }

    def invoke(methodName: String, parameter: AnyRef): this.type = {
        val methodOption = builderMethods.find(_.getName == methodName)
        if (methodOption.isDefined) {
            this.logging.logInfo(s"method named ${methodName.red} ${"found".green}")
            methodOption.get.invoke(this, parameter)
        } else {
            this.logging.logWarning(s"method named ${methodName.red} ${"not found".green}")
        }
        this
    }

}
