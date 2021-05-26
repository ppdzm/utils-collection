package io.github.ppdzm.utils.flink.scala.streaming

import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment._

/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait JavaFlinkStreaming[T] extends FlinkStreaming[T] {
    protected val streamExecutionEnvironment: StreamExecutionEnvironment =
        StreamExecutionEnvironment
            .getExecutionEnvironment
    //.setRuntimeMode(RuntimeExecutionMode.STREAMING)

    override def start(): Unit = {
        streamExecutionEnvironment.getConfig.setRestartStrategy(restartStrategyConfiguration)
        if (checkpointEnabled) {
            assert(checkpointConfiguration != null, "checkpointConfigItems need to be override")
            streamExecutionEnvironment.enableCheckpointing(checkpointConfiguration.checkpointIntervalInMilliseconds, checkpointConfiguration.checkpointMode)
            streamExecutionEnvironment.getCheckpointConfig.setCheckpointTimeout(checkpointConfiguration.checkpointTimeoutInMilliseconds)
            streamExecutionEnvironment.getCheckpointConfig.setMaxConcurrentCheckpoints(checkpointConfiguration.maxConcurrentCheckpoints)
            streamExecutionEnvironment.getCheckpointConfig.setMinPauseBetweenCheckpoints(checkpointConfiguration.minPauseBetweenCheckpoints)
            streamExecutionEnvironment.getCheckpointConfig.setTolerableCheckpointFailureNumber(checkpointConfiguration.tolerableCheckpointFailureNumber)
            streamExecutionEnvironment.getCheckpointConfig.enableExternalizedCheckpoints(checkpointConfiguration.cleanupMode)
            streamExecutionEnvironment.setStateBackend(checkpointConfiguration.stateBackend)
        }
        val dataStreamSource = streamExecutionEnvironment.addSource(dataSource)
        execute(dataStreamSource)
        streamExecutionEnvironment.execute(applicationName)
    }

    /**
     * 操作dataStreamSource
     *
     * @param dataStreamSource DataStreamSource[T]
     */
    def execute(dataStreamSource: DataStreamSource[T]): Unit
}
