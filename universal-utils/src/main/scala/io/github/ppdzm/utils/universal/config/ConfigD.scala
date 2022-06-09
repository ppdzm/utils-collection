package io.github.ppdzm.utils.universal.config

import java.util
import java.util.Properties

import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.cli.{CliUtils, ParameterOption, Render}
import io.github.ppdzm.utils.universal.core.CoreConstants
import io.github.ppdzm.utils.universal.feature.ExceptionGenerator
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.commons.cli.{CommandLine, DefaultParser, Options, PosixParser}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.{ClassTag, classTag}

/**
 * Created by Stuart Alex on 2016/4/7.
 * 读取resources下的配置文件，只读取被激活（active）的配置
 */
private[config] trait ConfigD extends Logging {
    protected val properties: Properties = initialize()
    protected val configKeyValues: mutable.HashMap[String, AnyRef] = mutable.HashMap[String, AnyRef]()
    private val replaceRegex = """\$\{[^#\}$]+\}""".r

    def getProperties: Properties = properties

    /**
     * 获取指定配置项的值
     * 参数的值可来源于且优先级为命令行参数>配置文件>系统变量
     *
     * @param property     配置项名称
     * @param defaultValue 配置项默认值
     * @param recursive    是否递归替换变量
     * @return
     */
    def getProperty(property: String, defaultValue: String = null, recursive: Boolean = true): String = {
        var plainValue = properties.getProperty(property, System.getProperty(property, defaultValue))
        if (plainValue == null)
            throw ExceptionGenerator.newException("PropertyMissing", s"Configuration $property is missing")
        if (recursive) {
            val missingRefs = mutable.MutableList[String]()
            var refs = findReferences(plainValue, missingRefs)
            while (refs.nonEmpty) {
                refs.foreach {
                    ref =>
                        val refKey = ref.substring(2, ref.length - 1)
                        val refValue = properties.getProperty(refKey, System.getProperty(refKey))
                        if (refValue.isNull) {
                            missingRefs += ref
                        } else {
                            plainValue = plainValue.replace(ref, refValue)
                        }
                }
                refs = findReferences(plainValue, missingRefs)
            }
            missingRefs.foreach {
                ref => logWarning(s"Value of reference $ref in configuration $property is not found, please confirm")
            }
        }
        if (plainValue == "") {
            logWarning(s"Value of configuration $property is empty, please confirm")
        } else {
            printConfig(property, plainValue)
        }
        plainValue
    }

    private def findReferences(plainValue: String, missingRefs: mutable.MutableList[String]): List[String] = {
        replaceRegex.findAllMatchIn(plainValue)
            .map { `match` => `match`.group(0) }
            .filterNot { ref => missingRefs.contains(ref) }
            .toList
    }

    /**
     * 获取指定配置项的值
     * 参数的值可来源于且优先级为命令行参数>配置文件>系统变量
     *
     * @param property 配置项名称
     * @return
     */
    def get[T: ClassTag](property: String): T = {
        val plainValue = properties.get(property)
        val x = classTag[T].runtimeClass
        if (x == classOf[util.HashMap[String, AnyRef]] && plainValue == null) {
            return new util.HashMap[String, AnyRef]() {
                val value: util.Map[AnyRef, AnyRef] = properties
                    .entrySet()
                    .filter(_.getKey.toString.startsWith(property + "."))
                    .map { e => e.getKey -> e.getValue }
                    .toMap
                    .asJava
                put(property, value)
            }
                .asInstanceOf[T]
        }
        if (plainValue.isNull)
            throw ExceptionGenerator.newException("PropertyMissing", s"Configuration $property is missing")
        printConfig(property, plainValue)
        plainValue.asInstanceOf[T]
    }

    private def printConfig[T: ClassTag](property: String, plainValue: AnyRef): Unit = {
        val messages = mutable.MutableList[(String, Render)]()
        messages += "Value of configuration " -> Render.GREEN
        messages += property -> Render.CYAN
        if (!configKeyValues.contains(property)) {
            configKeyValues(property) = plainValue
            messages += " is " -> Render.GREEN
            messages += plainValue.toString -> Render.CYAN
            logInfo(messages.toArray)
        } else if (configKeyValues(property) != plainValue) {
            configKeyValues(property) = plainValue
            messages += " is changed, now is " -> Render.GREEN
            messages += plainValue.toString -> Render.RED
            logInfo(messages.toArray)
        }
    }

    /**
     * 判断指定配置项是否存在
     *
     * @param property 配置项名称
     * @return
     */
    def isDefined(property: String): Boolean = {
        properties.containsKey(property)
    }

    /**
     * 返回配置中的键值
     *
     * @return
     */
    def keys: List[String] = properties.keySet().map(_.toString).toList

    /**
     * 解析以参数方式传入的options
     *
     * @param args 主程序参数
     */
    def parseArguments(args: Array[String]): Unit = {
        val properties = new Properties()
        CliUtils.parseArguments(args, properties)
        properties.foreach {
            case (key, value) => addProperty(key, value)
        }
    }

    /**
     * 解析程序参数
     *
     * @param args    程序参数
     * @param options 程序选项列表
     * @return
     */
    def parseOptions(args: Array[String], options: Options = null): CommandLine = {
        val cli = if (options.isNull)
            new PosixParser().parse(new Options().addOption(ParameterOption.option), args)
        else
            new PosixParser().parse(options.addOption(ParameterOption.option), args)
        val properties = cli.getOptionProperties(ParameterOption.name)
        properties.filter(p => p._1 == CoreConstants.PROFILE_ACTIVE_KEY).foreach(p => System.setProperty(p._1, p._2))
        properties.foreach(p => addProperty(p._1, p._2))
        cli
    }

    /**
     * 添加配置项
     *
     * @param property 配置项名称
     * @param value    配置项值
     * @return
     */
    def addProperty(property: String, value: Any): Unit = {
        properties.put(property, value.toString)
    }

    /**
     * 移除指定配置项，并返回该配置项的值
     *
     * @param property 配置项名称
     * @return
     */
    def removeProperty(property: AnyRef): AnyRef = {
        properties.remove(property)
    }

    protected def initialize(): Properties

    protected def refresh(): Unit
}
