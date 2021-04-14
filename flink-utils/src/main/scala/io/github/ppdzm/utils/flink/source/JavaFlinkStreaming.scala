package io.github.ppdzm.utils.flink.source

import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait JavaFlinkStreaming[T] extends FlinkStreaming[T] {
    protected val streamExecutionEnvironment: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment.setRuntimeMode(RuntimeExecutionMode.STREAMING)

    def main(args: Array[String]): Unit = {
        try {
            config.parseArguments(args)
            streamExecutionEnvironment.getConfig.setRestartStrategy(restartStrategyConfiguration)
            if (checkpointEnabled) {
                assert(checkpointConfigItems != null, "checkpointConfigItems need to be override")
                streamExecutionEnvironment.enableCheckpointing(checkpointConfigItems.checkpointIntervalInMilliseconds, checkpointConfigItems.checkpointMode)
                streamExecutionEnvironment.getCheckpointConfig.setCheckpointTimeout(checkpointConfigItems.checkpointTimeoutInMilliseconds)
                streamExecutionEnvironment.getCheckpointConfig.setMaxConcurrentCheckpoints(checkpointConfigItems.maxConcurrentCheckpoints)
                streamExecutionEnvironment.getCheckpointConfig.setMinPauseBetweenCheckpoints(checkpointConfigItems.minPauseBetweenCheckpoints)
                streamExecutionEnvironment.getCheckpointConfig.setTolerableCheckpointFailureNumber(checkpointConfigItems.tolerableCheckpointFailureNumber)
                streamExecutionEnvironment.getCheckpointConfig.enableExternalizedCheckpoints(checkpointConfigItems.cleanupMode)
                streamExecutionEnvironment.setStateBackend(checkpointConfigItems.stateBackend)
            }
            val dataStreamSource = streamExecutionEnvironment.addSource(dataSource)
            execute(dataStreamSource)
            streamExecutionEnvironment.execute(applicationName)
            logInfo(s"start execute application $applicationName")
        } catch {
            case e: Exception => alerter.alert(applicationName, e.getMessage)
                throw e
        }
    }

    def execute(dataStreamSource: DataStreamSource[T]): Unit
}
