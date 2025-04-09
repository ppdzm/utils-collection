package io.github.ppdzm.utils.flink.streaming.config;

import io.github.ppdzm.utils.flink.common.CheckpointConfiguration;
import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author Created by Stuart Alex on 2021/5/9.
 */
@NoArgsConstructor
public class FlinkStreamingConfig {
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
    public ConfigItem FLINK_RESTART_STRATEGY;
    public ConfigItem FLINK_RESTART_STRATEGY_FIXED_DELAY_ATTEMPTS;
    public ConfigItem FLINK_RESTART_STRATEGY_FIXED_DELAY_DELAY_INTERVAL_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_FAILURE_RATE;
    public ConfigItem FLINK_RESTART_STRATEGY_FAILURE_RATE_INTERVAL_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_FAILURE_RATE_DELAY_INTERVAL_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_INITIAL_BACKOFF_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_MAX_BACKOFF_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_BACKOFF_MULTIPLIER;
    public ConfigItem FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_RESET_BACKOFF_THRESHOLD_SECONDS;
    public ConfigItem FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_JITTER_FACTOR;

    public FlinkStreamingConfig(String applicationName, Config config) {
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
        this.FLINK_RESTART_STRATEGY = new ConfigItem(config, "flink.restart.strategy", "no");
        this.FLINK_RESTART_STRATEGY_FIXED_DELAY_ATTEMPTS = new ConfigItem(config, "flink.restart.strategy.fixed-delay.attempts", 2);
        this.FLINK_RESTART_STRATEGY_FIXED_DELAY_DELAY_INTERVAL_SECONDS = new ConfigItem(config, "flink.restart.strategy.fixed-delay.delay-interval.seconds", 5);
        this.FLINK_RESTART_STRATEGY_FAILURE_RATE = new ConfigItem(config, "flink.restart.strategy.failure-rate");
        this.FLINK_RESTART_STRATEGY_FAILURE_RATE_INTERVAL_SECONDS = new ConfigItem(config, "flink.restart.strategy.failure-rate.interval.seconds");
        this.FLINK_RESTART_STRATEGY_FAILURE_RATE_DELAY_INTERVAL_SECONDS = new ConfigItem(config, "flink.restart.strategy.failure-rate.delay-interval.seconds");
        this.FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_INITIAL_BACKOFF_SECONDS = new ConfigItem(config, "flink.restart.strategy.exponential-delay.initial-backoff.seconds");
        this.FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_MAX_BACKOFF_SECONDS = new ConfigItem(config, "flink.restart.strategy.exponential-delay.max-backoff.seconds");
        this.FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_BACKOFF_MULTIPLIER = new ConfigItem(config, "flink.restart.strategy.exponential-delay.backoff-multiplier");
        this.FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_RESET_BACKOFF_THRESHOLD_SECONDS = new ConfigItem(config, "flink.restart.strategy.exponential-delay.reset-backoff.threshold.seconds");
        this.FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_JITTER_FACTOR = new ConfigItem(config, "flink.restart.strategy.exponential-delay.jitter.factor");
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() throws Exception {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        String restartStrategy = FLINK_RESTART_STRATEGY.stringValue();
        RestartStrategies.RestartStrategyConfiguration restartStrategyConfiguration;
        switch (restartStrategy) {
            case "no":
                restartStrategyConfiguration = RestartStrategies.noRestart();
                break;
            case "fixed-delay":
                int attempts = FLINK_RESTART_STRATEGY_FIXED_DELAY_ATTEMPTS.intValue();
                int fixedDelayInterval = FLINK_RESTART_STRATEGY_FIXED_DELAY_DELAY_INTERVAL_SECONDS.intValue();
                restartStrategyConfiguration = (RestartStrategies.fixedDelayRestart(attempts, Time.seconds(fixedDelayInterval)));
                break;
            case "fallback":
                restartStrategyConfiguration = (RestartStrategies.fallBackRestart());
                break;
            case "failure-rate":
                int failureRate = FLINK_RESTART_STRATEGY_FAILURE_RATE.intValue();
                int failureRateInterval = FLINK_RESTART_STRATEGY_FAILURE_RATE_INTERVAL_SECONDS.intValue();
                int failureRateDelayInterval = FLINK_RESTART_STRATEGY_FAILURE_RATE_DELAY_INTERVAL_SECONDS.intValue();
                restartStrategyConfiguration = (RestartStrategies.failureRateRestart(failureRate, Time.seconds(failureRateInterval), Time.seconds(failureRateDelayInterval)));
                break;
            case "exponential-delay":
                int initialBackoff = FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_INITIAL_BACKOFF_SECONDS.intValue();
                int maxBackoff = FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_MAX_BACKOFF_SECONDS.intValue();
                double backoffMultiplier = Double.parseDouble(FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_BACKOFF_MULTIPLIER.stringValue());
                int resetBackoffThreshold = FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_RESET_BACKOFF_THRESHOLD_SECONDS.intValue();
                double jitterFactor = Double.parseDouble(FLINK_RESTART_STRATEGY_EXPONENTIAL_DELAY_JITTER_FACTOR.stringValue());
                restartStrategyConfiguration = (RestartStrategies.exponentialDelayRestart(Time.seconds(initialBackoff), Time.seconds(maxBackoff), backoffMultiplier, Time.seconds(resetBackoffThreshold), jitterFactor));
                break;
            default:
                throw new UnsupportedOperationException("restart strategy " + restartStrategy + " is unsupported. no, fixed-delay, fallback, failure-rate, exponential-delay are supported");
        }
        streamExecutionEnvironment.getConfig().setRestartStrategy(restartStrategyConfiguration);
        CheckpointConfiguration checkpointConfiguration = getCheckpointConfiguration();
        if (checkpointConfiguration != null && checkpointConfiguration.isCheckpointEnabled()) {
            streamExecutionEnvironment.enableCheckpointing(checkpointConfiguration.getCheckpointIntervalInMilliseconds(), checkpointConfiguration.getCheckpointMode());
            streamExecutionEnvironment.getCheckpointConfig().setCheckpointStorage(checkpointConfiguration.getCheckpointDir());
            streamExecutionEnvironment.getCheckpointConfig().setCheckpointTimeout(checkpointConfiguration.getCheckpointTimeoutInMilliseconds());
            streamExecutionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(checkpointConfiguration.getMaxConcurrentCheckpoints());
            streamExecutionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(checkpointConfiguration.getMinPauseBetweenCheckpoints());
            streamExecutionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(checkpointConfiguration.getTolerableCheckpointFailureNumber());
            streamExecutionEnvironment.getCheckpointConfig().enableExternalizedCheckpoints(checkpointConfiguration.getCleanupMode());
            streamExecutionEnvironment.setStateBackend(checkpointConfiguration.getStateBackend());

        }
        return streamExecutionEnvironment;
    }

    public CheckpointConfiguration getCheckpointConfiguration() throws Exception {
        if (FLINK_CHECKPOINT_ENABLED != null && FLINK_CHECKPOINT_ENABLED.booleanValue()) {
            String checkpointDir = FLINK_CHECKPOINT_DIR.stringValue();
            StateBackend stateBackend = new EmbeddedRocksDBStateBackend(true);
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
