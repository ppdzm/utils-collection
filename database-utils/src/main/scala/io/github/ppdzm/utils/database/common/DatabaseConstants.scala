package io.github.ppdzm.utils.database.common

import java.util.Properties

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
object DatabaseConstants {
    lazy val mySQLDefaultProperties: Properties = new Properties {
        put("autoReconnect", "true")
        put("characterEncoding", "utf8")
        put("driver", Drivers.MySQL.toString)
        put("failOverReadOnly", "false")
        put("rewriteBatchedStatements", "true")
        put("tinyInt1isBit", "false")
        put("transformedBitIsBoolean", "true")
        put("useSSL", "false")
        put("useunicode", "true")
        put("yearIsDateType", "false")
        put("zeroDateTimeBehavior", "convertToNull")
    }
}
