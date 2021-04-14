package io.github.ppdzm.utils.flink.common

import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
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
    val checkpointDir: String
    val checkpointIntervalInMilliseconds: Long
    val checkpointMode: CheckpointingMode
    val checkpointTimeoutInMilliseconds: Long
    val cleanupMode: CheckpointConfig.ExternalizedCheckpointCleanup
    val maxConcurrentCheckpoints: Int
    val minPauseBetweenCheckpoints: Int
    val tolerableCheckpointFailureNumber: Int
    val stateBackend: StateBackend
}
