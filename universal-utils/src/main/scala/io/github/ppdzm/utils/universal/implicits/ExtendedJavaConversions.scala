package io.github.ppdzm.utils.universal.implicits

import java.util.Properties

import io.github.ppdzm.utils.universal.base.ExceptionUtils

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
            ExceptionUtils.exceptionToString(throwable)
        }
    }

}
