package io.github.ppdzm.utils.universal.alert

/**
 * Created by Stuart Alex on 2021/4/12.
 */
class PrintAlerter extends Alerter {
    override def alert(subject: String, content: String): Unit = {
        println(s"【$subject】>>>>>$content")
    }
}
