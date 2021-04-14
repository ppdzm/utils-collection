package io.github.ppdzm.utils.universal.config

import java.io.{File, FileInputStream}
import java.net.URL
import java.util
import java.util.Properties

import io.github.ppdzm.utils.universal.base.ResourceUtils
import org.sa.utils.universal.base.ResourceUtils
import org.sa.utils.universal.core.CoreConstants._
import org.sa.utils.universal.core.SystemProperties._
import org.sa.utils.universal.implicits.BasicConversions._
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by Stuart Alex on 2016/4/7.
 */
object FileConfig extends Serializable {
    def apply(): FileConfig = {
        new FileConfig(configFileName, configFileExtension)
    }
}

class FileConfig(name: String, extension: String) extends Config {
    private lazy val fixedExtension =
        if (!extension.startsWith("."))
            "." + extension
        else
            extension
    private lazy val profileName = {
        val active = if (System.getProperty(profileActiveKey) == null) {
            // 若系统变量中没有设置，则尝试从默认配置文件中读取
            val maybeInHere = name + fixedExtension
            new Properties() {
                if (new File(maybeInHere).exists())
                    load(new FileInputStream(maybeInHere))
                else {
                    val inputStream = getClass.getClassLoader.getResourceAsStream(maybeInHere)
                    if (inputStream == null) {
                        logWarning(s"source file $maybeInHere can not be found")
                    } else {
                        load(inputStream)
                    }
                }
            }.getProperty(profileActiveKey, "")
        } else {
            System.getProperty(profileActiveKey, "")
        }
        logInfo(s"active profile is ${if (active.isEmpty) "empty string" else active}")
        if (name.isEmpty)
            s"$active$fixedExtension"
        else
            s"$name${if (active.isEmpty) "" else "-" + active}$fixedExtension"
    }

    protected override def refresh(): Unit = {
        this.properties.keys().foreach(removeProperty)
        initialize().foreach { case (k, v) => this.properties.put(k, v) }
    }

    protected override def initialize(): Properties = {
        logInfo(s"load config from file $profileName")
        val properties = new Properties()
        val url: URL = ResourceUtils.locateResourceAsURL(profileName)
        if (url != null) {
            val path = url.getPath
            logInfo(s"config file located at $path")
            val inputStream = url.openStream()
            fixedExtension match {
                case ".yaml" | ".yml" =>
                    val yaml = new Yaml()
                    properties.putAll(flat(null, yaml.load(inputStream)))
                case _ =>
                    properties.load(inputStream)
                    properties.setProperty(profilePathKey, path.substring(0, path.lastIndexOf("/")))
            }
        } else {
            logWarning(s"config file $profileName not found, using only default configurations defined in code")
        }
        properties
    }

    private def flat(parent: String, obj: AnyRef): mutable.Map[String, AnyRef] = {
        val mutableMap = mutable.Map[String, AnyRef]()
        obj match {
            case m: util.HashMap[String, AnyRef] =>
                m.foreach {
                    case (k, v) =>
                        val newParent = if (parent.isNullOrEmpty)
                            k
                        else
                            parent + "." + k
                        mutableMap.putAll(flat(newParent, v))
                }
            case _ => mutableMap.put(parent, obj)
        }
        mutableMap
    }


}
