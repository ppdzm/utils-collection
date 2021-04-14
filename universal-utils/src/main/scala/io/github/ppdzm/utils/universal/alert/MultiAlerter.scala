package io.github.ppdzm.utils.universal.alert

/**
 * Created by Stuart Alex on 2021/4/12.
 */
class MultiAlerter(alerterList: Array[Alerter]) extends Alerter {
    override def alert(subject: String, content: String): Unit = {
        alerterList.foreach(_.alert(subject, content))
    }
}