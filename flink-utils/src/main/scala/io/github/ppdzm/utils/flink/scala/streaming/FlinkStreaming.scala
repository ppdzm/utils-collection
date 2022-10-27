package io.github.ppdzm.utils.flink.scala.streaming

import io.github.ppdzm.utils.flink.scala.common.CheckpointConfiguration
import io.github.ppdzm.utils.universal.alert.Alerter
import io.github.ppdzm.utils.universal.base.Logging
import org.apache.flink.api.common.restartstrategy.RestartStrategies.RestartStrategyConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction


/**
 * Created by Stuart Alex on 2021/4/13.
 */
trait FlinkStreaming[T] {
    protected lazy val logging = new Logging(getClass)
    /**
     * Checkpoint配置
     */
    protected lazy val checkpointConfiguration: CheckpointConfiguration = null
    /**
     * 应用名称
     */
    protected val applicationName: String
    /**
     * 告警器
     */
    protected val alerter: Alerter
    /**
     * 是否启用Checkpoint
     */
    protected val checkpointEnabled: Boolean
    /**
     * Flink应用重启配置
     */
    protected val restartStrategyConfiguration: RestartStrategyConfiguration
    /**
     * 数据源方法
     */
    protected val dataSource: SourceFunction[T]
    /**
     * 命令行参数
     */
    private var _args: Array[String] = _

    def main(args: Array[String]): Unit = {
        try {
            _args = args
            Logging.setLogging2Stdout(true)
            start()
            this.logging.logInfo(s"start execute application $applicationName")
        } catch {
            case e: Exception =>
                alerter.alert("", applicationName, e);
        }
    }

    protected def args: Array[String] = _args

    protected def start()
}
