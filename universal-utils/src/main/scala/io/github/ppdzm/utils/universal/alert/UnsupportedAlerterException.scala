package io.github.ppdzm.utils.universal.alert

/**
 * Created by Stuart Alex on 2021/4/12.
 */
class UnsupportedAlerterException(alerterType: String) extends Exception(s"Unsupported Alerter type: $alerterType")
