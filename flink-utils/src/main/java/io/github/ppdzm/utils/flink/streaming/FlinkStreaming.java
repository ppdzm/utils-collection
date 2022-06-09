package io.github.ppdzm.utils.flink.streaming;

import io.github.ppdzm.utils.flink.common.CheckpointConfiguration;
import io.github.ppdzm.utils.flink.streaming.config.FlinkStreamingConfig;
import io.github.ppdzm.utils.universal.alert.Alerter;
import io.github.ppdzm.utils.universal.base.Logging;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.Serializable;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class FlinkStreaming<T> extends Logging implements Serializable {
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 告警器
     */
    private Alerter alerter;
    /**
     * Checkpoint配置
     */
    private CheckpointConfiguration checkpointConfiguration;
    private FlinkStreamingConfig flinkStreamingConfig;

    public FlinkStreaming(FlinkStreamingConfig flinkStreamingConfig, Alerter alerter) throws Exception {
        this();
        this.applicationName = flinkStreamingConfig.applicationName;
        this.alerter = alerter;
        this.checkpointConfiguration = flinkStreamingConfig.getCheckpointConfiguration();
        this.flinkStreamingConfig = flinkStreamingConfig;
    }

    public void start(SourceFunction<T> sourceFunction) throws Exception {
        try {
            StreamExecutionEnvironment streamExecutionEnvironment = flinkStreamingConfig.getStreamExecutionEnvironment();
            DataStreamSource<T> dataStreamSource = streamExecutionEnvironment.addSource(sourceFunction);
            execute(dataStreamSource);
            streamExecutionEnvironment.execute(applicationName);
            logInfo("start execute application " + applicationName);
        } catch (Exception e) {
            alerter.alert("", applicationName, e);
            throw e;
        }
    }

    /**
     * 执行对dataStreamSource进行的操作
     *
     * @param dataStreamSource 数据源
     */
    public abstract void execute(DataStreamSource<T> dataStreamSource);

}
