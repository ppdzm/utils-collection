package io.github.ppdzm.utils.flink.source

import io.github.ppdzm.utils.flink.common.CheckpointConfigItems
import io.github.ppdzm.utils.universal.alert.Alerter
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.config.Config
import org.apache.flink.api.common.restartstrategy.RestartStrategies.RestartStrategyConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.kafka.clients.consumer.OffsetResetStrategy

/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait FlinkStreaming[T] extends Logging {
    /**
     * Checkpoint配置
     */
    protected lazy val checkpointConfigItems: CheckpointConfigItems = null
    /**
     * 应用名称
     */
    protected val applicationName: String
    /**
     * 告警器
     */
    protected val alerter: Alerter
    /**
     * 配置
     */
    protected val config: Config
    /**
     * 是否启用Checkpoint
     */
    protected val checkpointEnabled: Boolean
    /**
     * Kafka消费者OffsetResetStrategy
     */
    protected val offsetResetStrategy: OffsetResetStrategy
    /**
     * Flink应用重启配置
     */
    protected val restartStrategyConfiguration: RestartStrategyConfiguration
    /**
     * 数据源方法
     */
    protected val dataSource: SourceFunction[T]
}
