package io.github.ppdzm.utils.universal.cli

import java.util.Properties

import io.github.ppdzm.utils.universal.base.Enum
import io.github.ppdzm.utils.universal.config.Config
import io.github.ppdzm.utils.universal.core.SystemProperties
import org.sa.utils.universal.base.Enum
import org.sa.utils.universal.config.Config
import org.sa.utils.universal.core.SystemProperties
import org.sa.utils.universal.implicits.BasicConversions._

/**
 * Created by Stuart Alex on 2017/9/6.
 */
trait CommonParameter extends Enum {

    def validateParameters(config: Config): Unit = this.values.foreach(value => assert(value.getValue(config).notNullAndEmpty, s"$value is missing"))

    def validateParameters(properties: Properties): Unit = this.values.foreach(value => assert(value.getValue(properties).notNullAndEmpty, s"$value is missing"))

    override def toString(): String = {
        this.values.map(e => {
            if (this.defaultValues.contains(e))
                s"$e($word${this.defaultValues(e)})"
            else
                e
        }).mkString(", ")
    }

    private def word: String = SystemProperties.language match {
        case "zh" => "默认值为"
        case _ => "with default value "
    }

    def defaultValues: Map[Value, String] = Map[Value, String]()

    implicit class ParameterValue(value: Value) {

        def getValue(config: Config): String = {
            if (defaultValues.contains(value))
                config.newConfigItem(value.toString, defaultValues(value)).stringValue
            else
                config.newConfigItem(value.toString).stringValue
        }

        def getValue(properties: Properties): String = {
            if (defaultValues.contains(value))
                properties.getOrDefault(value.toString, defaultValues(value)).toString
            else
                properties.getProperty(value.toString)
        }
    }

}