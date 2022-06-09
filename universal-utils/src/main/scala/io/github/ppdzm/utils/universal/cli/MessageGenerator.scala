package io.github.ppdzm.utils.universal.cli

import java.nio.charset.StandardCharsets
import java.text.{ChoiceFormat, MessageFormat}
import java.util.{Locale, ResourceBundle}

import io.github.ppdzm.utils.universal.core.SystemProperties

import scala.collection.mutable
import scala.util.Try

/**
 * Created by Stuart Alex on 2017/9/8.
 */
object MessageGenerator {
    private val resourceBundles = mutable.Map[String, ResourceBundle]()

    def generate(name: String, int: Int): String = {
        val resourceBundle = this.getResourceBundle
        if (resourceBundle != null && resourceBundle.containsKey(name)) {
            val pattern = new String(resourceBundle.getString(name).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
            Try(MessageFormat.format(new ChoiceFormat(pattern).format(int), new Integer(int)))
                .getOrElse(generate(name, int.toString))
        }
        else
            "Message missing: " + name + ":" + int
    }

    def generate(name: String, args: Object*): String = {
        val resourceBundle = this.getResourceBundle
        if (resourceBundle != null && resourceBundle.containsKey(name)) {
            val pattern = new String(resourceBundle.getString(name).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
            MessageFormat.format(pattern, args: _*)
        }
        else
            "Message missing: " + name + " " + args.mkString(" ")
    }

    private def getResourceBundle: ResourceBundle = {
        val language = SystemProperties.language
        if (!resourceBundles.contains(language)) {
            try {
                val resourceBundle = ResourceBundle.getBundle(s"message", Locale.forLanguageTag(language))
                resourceBundles += language -> resourceBundle
            } catch {
                case _: Exception =>
            }
        }
        resourceBundles.get(language).orNull
    }

}
