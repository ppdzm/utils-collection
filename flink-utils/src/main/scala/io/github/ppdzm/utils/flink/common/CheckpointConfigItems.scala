package io.github.ppdzm.utils.flink.common

import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.CheckpointingMode._
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup

/**
 * Created by Stuart Alex on 2021/4/6.
 */
class SampleCheckpointConfigItems(override val checkpointDir: String) extends CheckpointConfigItems {
    override val checkpointIntervalInMilliseconds: Long = 5000
    override val checkpointMode: CheckpointingMode = CheckpointingMode.EXACTLY_ONCE
    override val checkpointTimeoutInMilliseconds: Long = 60000
    override val cleanupMode: ExternalizedCheckpointCleanup = ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
    override val maxConcurrentCheckpoints: Int = 1
    override val minPauseBetweenCheckpoints: Int = 500
    override val tolerableCheckpointFailureNumber: Int = 10
    override val stateBackend: StateBackend = new FsStateBackend("file:////tmp/checkpoints")
}

abstract class CheckpointConfigItems {
    /**
     * Checkpoint所在目录，本地文件或HDFS
     */
    val checkpointDir: String
    /**
     * Checkpoint两次启动间时间间隔（毫秒）
     */
    val checkpointIntervalInMilliseconds: Long
    /**
     * Checkpoint模式：[[EXACTLY_ONCE]]、[[AT_LEAST_ONCE]]
     */
    val checkpointMode: CheckpointingMode
    /**
     * Checkpoint超时（毫秒）
     */
    val checkpointTimeoutInMilliseconds: Long
    /**
     * 作业取消时Checkpoint的清理模式：DELETE_ON_CANCELLATION、RETAIN_ON_CANCELLATION
     */
    val cleanupMode: CheckpointConfig.ExternalizedCheckpointCleanup
    /**
     * 最大同时运行的Checkpoint
     */
    val maxConcurrentCheckpoints: Int
    /**
     * 上次Checkpoint结束和本次Checkpoint启动之间的时间间隔
     */
    val minPauseBetweenCheckpoints: Int
    /**
     * 可容忍的Checkpoint失败次数
     */
    val tolerableCheckpointFailureNumber: Int
    /**
     * 状态后端
     */
    val stateBackend: StateBackend
}
