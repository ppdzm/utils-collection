package io.github.ppdzm.utils.flink.common;

import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;

import java.io.File;

/**
 * @author Created by Stuart Alex on 2021/5/10.
 */
public class SimpleCheckpointConfiguration extends CheckpointConfiguration {

    public SimpleCheckpointConfiguration() {
        super(
            true,
            "file:///" + new File(".").getAbsolutePath() + "/data/checkpoints",
            10000L,
            60000L,
            1,
            500,
            10,
            CheckpointingMode.EXACTLY_ONCE,
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION,
            new FsStateBackend("file:///" + new File(".").getAbsolutePath() + "/data/checkpoints")
        );
    }

}
