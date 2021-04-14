package io.github.ppdzm.utils.universal.config

import java.io.{File, FileInputStream}
import java.util.Properties

import com.typesafe.config.ConfigFactory
import io.github.ppdzm.utils.universal.core.CoreConstants.profileActiveKey

/**
 * Created by Stuart Alex on 2021/4/6.
 */
class TypeSafeFileConfig(name: String, extension: String) extends Config {
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
    private lazy val config = ConfigFactory.load(profileName)

    override protected def initialize(): Properties = new Properties()

    override protected def refresh(): Unit = {

    }
}
