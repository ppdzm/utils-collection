package io.github.ppdzm.utils.universal.implicits

import java.util.Properties

import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2021/3/30.
 */
object ExtendedJavaConversions {

    implicit class PropertiesImplicits(properties: Properties) {

        def toKeyValuePair: Array[(String, String)] = {
            properties.keySet().map(key => key.toString -> properties.get(key).toString).toArray
        }

    }

    implicit class ThrowableImplicits(throwable: Throwable) {
        def toDetailedString: String = {
            val message = new StringBuilder
            message.append(throwable.toString)
            for (element <- throwable.getStackTrace) {
                message.append("\n\tat ").append(element.toString)
            }
            message.append("\n")
            for (throwable <- throwable.getSuppressed) {
                message.append(throwable.toDetailedString)
            }
            message.append("\n")
            val cause = throwable.getCause
            if (cause != null)
                message.append(cause.toDetailedString)
            return message.toString
        }
    }

}
