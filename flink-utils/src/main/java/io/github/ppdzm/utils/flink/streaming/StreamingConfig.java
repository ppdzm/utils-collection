package io.github.ppdzm.utils.flink.streaming;

import io.github.ppdzm.utils.flink.common.CheckpointConfiguration;
import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class StreamingConfig {
    public String applicationName;
    public ConfigItem FLINK_CHECKPOINT_ENABLED;
    public ConfigItem FLINK_CHECKPOINT_DIR;
    public ConfigItem FLINK_CHECKPOINT_INTERVAL;
    public ConfigItem FLINK_CHECKPOINT_MODE;
    public ConfigItem FLINK_CHECKPOINT_TIMEOUT;
    public ConfigItem FLINK_CHECKPOINT_CLEANUP_MODE;
    public ConfigItem FLINK_CHECKPOINT_MAX_CONCURRENT;
    public ConfigItem FLINK_CHECKPOINT_MIN_PAUSE;
    public ConfigItem FLINK_CHECKPOINT_TOLERABLE_FAILURE;
    public ConfigItem FLINK_CHECKPOINT_STATE_TYPE;

    public StreamingConfig(String applicationName, Config config) {
        this.applicationName = applicationName;
        this.FLINK_CHECKPOINT_ENABLED = new ConfigItem(config, "flink.checkpoint.enabled", true);
        this.FLINK_CHECKPOINT_DIR = new ConfigItem(config, "flink.checkpoint.dir");
        this.FLINK_CHECKPOINT_INTERVAL = new ConfigItem(config, "flink.checkpoint.interval", 5000);
        this.FLINK_CHECKPOINT_MODE = new ConfigItem(config, "flink.checkpoint.mode", "exactly_once");
        this.FLINK_CHECKPOINT_TIMEOUT = new ConfigItem(config, "flink.checkpoint.timeout", 60000);
        this.FLINK_CHECKPOINT_CLEANUP_MODE = new ConfigItem(config, "flink.checkpoint.cleanup-mode", "retain_on_cancellation");
        this.FLINK_CHECKPOINT_MAX_CONCURRENT = new ConfigItem(config, "flink.checkpoint.max-concurrent", 1);
        this.FLINK_CHECKPOINT_MIN_PAUSE = new ConfigItem(config, "flink.checkpoint.min-pause", 500);
        this.FLINK_CHECKPOINT_TOLERABLE_FAILURE = new ConfigItem(config, "flink.checkpoint.tolerable-failure", 10);
        this.FLINK_CHECKPOINT_STATE_TYPE = new ConfigItem(config, "flink.checkpoint.state.type", "fs");
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() throws Exception {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        streamExecutionEnvironment.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(2, Time.seconds(2)));
        CheckpointConfiguration checkpointConfiguration = getCheckpointConfigItems();
        if (checkpointConfiguration != null && checkpointConfiguration.isCheckpointEnabled()) {
            streamExecutionEnvironment.enableCheckpointing(checkpointConfiguration.getCheckpointIntervalInMilliseconds(), checkpointConfiguration.getCheckpointMode());
            streamExecutionEnvironment.getCheckpointConfig().setCheckpointTimeout(checkpointConfiguration.getCheckpointTimeoutInMilliseconds());
            streamExecutionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(checkpointConfiguration.getMaxConcurrentCheckpoints());
            streamExecutionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(checkpointConfiguration.getMinPauseBetweenCheckpoints());
            streamExecutionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(checkpointConfiguration.getTolerableCheckpointFailureNumber());
            streamExecutionEnvironment.getCheckpointConfig().enableExternalizedCheckpoints(checkpointConfiguration.getCleanupMode());
            streamExecutionEnvironment.setStateBackend(checkpointConfiguration.getStateBackend());
        }
        return streamExecutionEnvironment;
    }

    public CheckpointConfiguration getCheckpointConfigItems() throws Exception {
        if (FLINK_CHECKPOINT_ENABLED.booleanValue()) {
            String checkpointDir = FLINK_CHECKPOINT_DIR.stringValue();
            StateBackend stateBackend;
            switch (FLINK_CHECKPOINT_STATE_TYPE.stringValue()) {
                case "fs":
                    stateBackend = new FsStateBackend(checkpointDir);
                    break;
                case "rocksdb":
                    stateBackend = new RocksDBStateBackend(checkpointDir, true);
                    break;
                default:
                    stateBackend = new MemoryStateBackend();
                    break;
            }
            return CheckpointConfiguration
                .builder()
                .checkpointDir(checkpointDir)
                .checkpointIntervalInMilliseconds(FLINK_CHECKPOINT_INTERVAL.longValue())
                .checkpointMode(CheckpointingMode.valueOf(FLINK_CHECKPOINT_MODE.stringValue().toUpperCase()))
                .checkpointTimeoutInMilliseconds(FLINK_CHECKPOINT_TIMEOUT.longValue())
                .cleanupMode(org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup.valueOf(FLINK_CHECKPOINT_CLEANUP_MODE.stringValue().toUpperCase()))
                .maxConcurrentCheckpoints(FLINK_CHECKPOINT_MAX_CONCURRENT.intValue())
                .minPauseBetweenCheckpoints(FLINK_CHECKPOINT_MIN_PAUSE.intValue())
                .stateBackend(stateBackend)
                .tolerableCheckpointFailureNumber(FLINK_CHECKPOINT_TOLERABLE_FAILURE.intValue())
                .checkpointEnabled(true)
                .build();
        } else {
            return null;
        }
    }

}
