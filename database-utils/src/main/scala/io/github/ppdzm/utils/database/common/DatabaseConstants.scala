package io.github.ppdzm.utils.database.common

import java.util.Properties

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
object DatabaseConstants {
    lazy val mySQLDefaultProperties: Properties = new Properties {
        put("driver", Drivers.MySQL.toString)
        put("useunicode", "true")
        put("characterEncoding", "utf8")
        put("autoReconnect", "true")
        put("failOverReadOnly", "false")
        put("zeroDateTimeBehavior", "convertToNull")
        put("transformedBitIsBoolean", "true")
        put("tinyInt1isBit", "false")
        put("useSSL", "false")
    }
}
