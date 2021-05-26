package io.github.ppdzm.utils.flink.source.kafka

import io.github.ppdzm.utils.flink.scala.streaming.kafka.JavaAvroArrayRecordFlinkKafkaStreaming
import io.github.ppdzm.utils.universal.alert.{Alerter, LoggerAlerter}
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import io.github.ppdzm.utils.universal.formats.avro.AvroUtils
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.scalatest.FunSuite

import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2021/4/13.
 */
class FlinkKafkaStreamingTest extends FunSuite with JavaAvroArrayRecordFlinkKafkaStreaming {
    override protected val applicationName: String = "flink-kafka-streaming-test"
    override protected val alerter: Alerter = new LoggerAlerter()
    override protected val checkpointEnabled: Boolean = false
    override protected val offsetResetStrategy: OffsetResetStrategy = OffsetResetStrategy.EARLIEST
    override protected val restartStrategyConfiguration: RestartStrategies.RestartStrategyConfiguration = RestartStrategies.fixedDelayRestart(2, Time.seconds(2))
    override protected val schema: Schema = AvroUtils.getSchemaFromFile("../data/avsc/schema-all.json")
    override protected val schemaRegistryUrl: String = "http://10.25.21.41:8081"
    override protected val kafkaSourceTopic: String = "data_buffer_uat_dc_sdk_dev"
    override protected val kafkaBrokers: String = "10.25.21.41:9092,10.25.21.42:9092,10.25.21.43:9092"
    override protected val autoCommit: Boolean = true
    override protected val consumerGroupId: String = "flink-kafka-streaming-test"
    override protected val additionalConsumerConfig: Map[String, AnyRef] = null

    override def execute(dataStreamSource: DataStreamSource[GenericData.Array[GenericRecord]]): Unit = {
        dataStreamSource
            .addSink(new SinkFunction[GenericData.Array[GenericRecord]] {
                override def invoke(value: GenericData.Array[GenericRecord]): Unit = {
                    value.foreach(println)
                }
            })
    }

    test("") {
        this.main(null)
    }

}
