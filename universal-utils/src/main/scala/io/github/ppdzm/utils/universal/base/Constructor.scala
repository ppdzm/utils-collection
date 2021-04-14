package io.github.ppdzm.utils.universal.base

import java.sql.Date
import java.util

object Constructor {

    def construct(obj: AnyRef, columnsMap: util.Map[String, Any]): Unit = {
        obj.getClass.getDeclaredFields.foreach {
            field =>
                val columnName = field.getName
                if (columnsMap.containsKey(columnName) && columnsMap.get(columnName) != null) {
                    field.setAccessible(true)
                    field.getType.getSimpleName.toLowerCase match {
                        case "integer" => field.set(obj, columnsMap.get(columnName).asInstanceOf[Int])
                        case "long" => field.set(obj, columnsMap.get(columnName).asInstanceOf[Long])
                        case "date" => field.set(obj, Date.valueOf(columnsMap.get(columnName).toString))
                        case _ => field.set(obj, columnsMap.get(columnName))
                    }
                }
        }
    }

}
