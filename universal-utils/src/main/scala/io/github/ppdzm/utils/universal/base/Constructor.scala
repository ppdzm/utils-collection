package io.github.ppdzm.utils.universal.base

import java.sql.Date

object Constructor {

    def construct(obj: AnyRef, fieldsNameValuePair: Map[String, Any]): Unit = {
        obj.getClass.getDeclaredFields.foreach {
            field =>
                val columnName = field.getName
                if (fieldsNameValuePair.contains(columnName) && fieldsNameValuePair(columnName) != null) {
                    field.setAccessible(true)
                    field.getType.getSimpleName.toLowerCase match {
                        case "integer" => field.set(obj, fieldsNameValuePair.get(columnName).asInstanceOf[Int])
                        case "long" => field.set(obj, fieldsNameValuePair.get(columnName).asInstanceOf[Long])
                        case "date" => field.set(obj, Date.valueOf(fieldsNameValuePair.get(columnName).toString))
                        case _ => field.set(obj, fieldsNameValuePair.get(columnName))
                    }
                }
        }
    }

}
