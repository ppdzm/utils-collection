package io.github.ppdzm.utils.hadoop.scala.yarn

import io.github.ppdzm.utils.universal.base.Enum

/**
 * Created by Stuart Alex on 2017/9/6.
 */
object ApplicationState extends Enum {
    val submitted: Value = Value("submitted")
    val accepted: Value = Value("accepted")
    val running: Value = Value("running")
    val finished: Value = Value("finished")
    val failed: Value = Value("failed")
    val killed: Value = Value("killed")
    val all: Value = Value("")
}
