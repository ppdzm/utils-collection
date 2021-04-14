package io.github.ppdzm.utils.universal.alert

import io.github.ppdzm.utils.universal.base.Logging
import org.sa.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2021/4/12.
 */
class LoggerAlerter extends Alerter with Logging {
    override def alert(subject: String, content: String): Unit = {
        logError(s"【$subject】>>>>>$content")
    }
}
