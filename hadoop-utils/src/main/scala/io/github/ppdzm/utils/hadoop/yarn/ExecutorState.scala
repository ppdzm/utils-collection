package io.github.ppdzm.utils.hadoop.yarn

import io.github.ppdzm.utils.universal.base.Enum

/**
 * Created by Stuart Alex on 2017/9/6.
 */
object ExecutorState extends Enum {
    val active: Value = Value("active")
    val dead: Value = Value("dead")
    val both: Value = Value("both")
}
