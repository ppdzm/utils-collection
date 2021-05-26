package io.github.ppdzm.utils.flink.scala.streaming

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait ScalaFlinkStreaming[T] extends FlinkStreaming[T] {
    protected val streamExecutionEnvironment: StreamExecutionEnvironment =
        StreamExecutionEnvironment
            .getExecutionEnvironment
    //.setRuntimeMode(RuntimeExecutionMode.STREAMING)

    override def start(): Unit = {
        streamExecutionEnvironment.getConfig.setRestartStrategy(restartStrategyConfiguration)
        if (checkpointEnabled) {
            assert(checkpointConfiguration != null, "checkpointConfiguration need to be override")
            streamExecutionEnvironment.enableCheckpointing(checkpointConfiguration.checkpointIntervalInMilliseconds, checkpointConfiguration.checkpointMode)
            streamExecutionEnvironment.getCheckpointConfig.setCheckpointTimeout(checkpointConfiguration.checkpointTimeoutInMilliseconds)
            streamExecutionEnvironment.getCheckpointConfig.setMaxConcurrentCheckpoints(checkpointConfiguration.maxConcurrentCheckpoints)
            streamExecutionEnvironment.getCheckpointConfig.setMinPauseBetweenCheckpoints(checkpointConfiguration.minPauseBetweenCheckpoints)
            streamExecutionEnvironment.getCheckpointConfig.setTolerableCheckpointFailureNumber(checkpointConfiguration.tolerableCheckpointFailureNumber)
            streamExecutionEnvironment.getCheckpointConfig.enableExternalizedCheckpoints(checkpointConfiguration.cleanupMode)
            streamExecutionEnvironment.setStateBackend(checkpointConfiguration.stateBackend)
        }
        execute(streamExecutionEnvironment, dataSource)
        streamExecutionEnvironment.execute(applicationName)
    }

    /**
     * 操作dataStream
     */
    def execute(streamExecutionEnvironment: StreamExecutionEnvironment, dataSource: SourceFunction[T]): Unit
}
