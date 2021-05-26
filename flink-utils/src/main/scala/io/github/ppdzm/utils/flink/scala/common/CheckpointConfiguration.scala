package io.github.ppdzm.utils.flink.scala.common

import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.CheckpointingMode._
import org.apache.flink.streaming.api.environment.CheckpointConfig

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
trait CheckpointConfiguration {
    /**
     * Checkpoint是否启用
     */
    val checkpointEnabled: Boolean
    /**
     * Checkpoint所在目录，本地文件或HDFS
     */
    val checkpointDir: String
    /**
     * Checkpoint两次启动间时间间隔（毫秒）
     */
    val checkpointIntervalInMilliseconds: Long
    /**
     * Checkpoint超时（毫秒）
     */
    val checkpointTimeoutInMilliseconds: Long
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
     * Checkpoint模式：[[EXACTLY_ONCE]]、[[AT_LEAST_ONCE]]
     */
    val checkpointMode: CheckpointingMode
    /**
     * 作业取消时Checkpoint的清理模式：DELETE_ON_CANCELLATION、RETAIN_ON_CANCELLATION;
     */
    val cleanupMode: CheckpointConfig.ExternalizedCheckpointCleanup
    /**
     * 状态后端
     */
    val stateBackend: StateBackend
}
