package io.github.ppdzm.utils.flink.source

import io.github.ppdzm.utils.flink.common.CheckpointConfigItems
import io.github.ppdzm.utils.universal.alert.Alerter
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.config.Config
import org.apache.flink.api.common.restartstrategy.RestartStrategies.RestartStrategyConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.sa.utils.universal.alert.Alerter
import org.sa.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait FlinkStreaming[T] extends Logging {
    protected val applicationName: String
    protected val alerter: Alerter
    protected val config: Config
    protected val checkpointEnabled: Boolean
    protected lazy val checkpointConfigItems: CheckpointConfigItems = null
    protected val offsetResetStrategy: OffsetResetStrategy
    protected val restartStrategyConfiguration: RestartStrategyConfiguration
    protected val dataSource: SourceFunction[T]
}
