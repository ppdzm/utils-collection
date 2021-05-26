package io.github.ppdzm.utils.flink.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
@AllArgsConstructor
@Builder
@Data
public class CheckpointConfiguration {
    /**
     * Checkpoint是否启用
     */
    private boolean checkpointEnabled;
    /**
     * Checkpoint所在目录，本地文件或HDFS
     */
    private String checkpointDir;
    /**
     * Checkpoint两次启动间时间间隔（毫秒）
     */
    private long checkpointIntervalInMilliseconds;
    /**
     * Checkpoint超时（毫秒）
     */
    private long checkpointTimeoutInMilliseconds;
    /**
     * 最大同时运行的Checkpoint
     */
    private int maxConcurrentCheckpoints;
    /**
     * 上次Checkpoint结束和本次Checkpoint启动之间的时间间隔
     */
    private int minPauseBetweenCheckpoints;
    /**
     * 可容忍的Checkpoint失败次数
     */
    private int tolerableCheckpointFailureNumber;
    /**
     * Checkpoint模式：[[EXACTLY_ONCE]]、[[AT_LEAST_ONCE]]
     */
    private CheckpointingMode checkpointMode;
    /**
     * 作业取消时Checkpoint的清理模式：DELETE_ON_CANCELLATION、RETAIN_ON_CANCELLATION;
     */
    private CheckpointConfig.ExternalizedCheckpointCleanup cleanupMode;
    /**
     * 状态后端
     */
    private StateBackend stateBackend;

    public CheckpointConfiguration() {
        this.checkpointEnabled = false;
    }
}
