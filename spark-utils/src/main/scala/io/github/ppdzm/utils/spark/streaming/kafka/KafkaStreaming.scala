package io.github.ppdzm.utils.spark.streaming.kafka

import io.github.ppdzm.utils.universal.alert.Alerter
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.sa.utils.hadoop.constants.KafkaConfigConstants
import org.sa.utils.spark.common.SparkStreamingEnvironment
import org.sa.utils.spark.streaming.{PartitionProcessor, RDDProcessor, ResultProcessor}

import scala.collection.mutable

/**
 * * @author StuartAlex on 2019-08-06 17:45
 *
 * @tparam K  ConsumerRecord Key
 * @tparam V  ConsumerRecord Value
 * @tparam R1 RDDProcessor返回的RDD类型，同时也是PartitionProcessor的输入类型
 * @tparam R2 PartitionProcessor的返回类型
 */
abstract class KafkaStreaming[K, V, R1, R2] extends KafkaConfigConstants with SparkStreamingEnvironment with RDDProcessor[ConsumerRecord[K, V], R1] {
    lazy val kafkaParams: mutable.Map[String, Object] = mutable.Map[String, Object](
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> kafkaBrokers,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> kafkaKeyDeserializer,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> kafkaValueDeserializer,
        ConsumerConfig.GROUP_ID_CONFIG -> kafkaConsumerGroupId,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> kafkaOffsetConfig,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean),
        ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG -> (10 * 1024 * 1024: java.lang.Integer)
    )
    override protected lazy val sparkConf: SparkConf = new SparkConf()
        .setMaster(SPARK_MASTER.stringValue)
        .setAppName(SPARK_APP_NAME.stringValue)
        .set("spark.streaming.backpressure.enabled", KAFKA_BACKPRESSURE_ENABLED.stringValue)
        .set("spark.streaming.kafka.maxRatePerPartition", KAFKA_MAX_RATE_PER_PARTITION.stringValue)
        .set("spark.streaming.kafka.consumer.poll.ms", KAFKA_CONSUMER_POLL_MS.stringValue)
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryo.registrationRequired", "true")
        .registerKryoClasses(Array(
            Class.forName("scala.collection.immutable.Map$EmptyMap$")
        ))
    protected lazy val stream: InputDStream[ConsumerRecord[K, V]] = KafkaUtils.createDirectStream[K, V](
        streamingContext,
        PreferConsistent,
        Subscribe[K, V](kafkaTopics, kafkaParams)
    )
    protected val alerter: Alerter
    protected val applicationName: String
    protected val kafkaTopics: Array[String]
    protected val kafkaBrokers: String
    protected val kafkaConsumerGroupId: String
    protected val kafkaKeyDeserializer: String = classOf[StringDeserializer].getName
    protected val kafkaValueDeserializer: String = classOf[StringDeserializer].getName
    protected val kafkaOffsetConfig: String

    def startWithProcessor(partitionProcessor: PartitionProcessor[R1, R2], resultProcessor: ResultProcessor[R2]): Unit = {
        startWithProcessFunction(partitionProcessor.processPartition, resultProcessor.processResult)
    }

    def startWithProcessFunction(partitionProcessFunction: Iterator[R1] => R2, resultProcessFunction: R2 => Unit): Unit = {
        stream.foreachRDD {
            rdd =>
                val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                try {
                    processRDD(rdd).foreachPartition {
                        partition =>
                            val result = partitionProcessFunction(partition)
                            resultProcessFunction(result)
                    }
                } catch {
                    case e: Exception =>
                        alerter.alert(s"spark streaming application $applicationName error", e.toString)
                        throw e
                }
                stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
        }
        streamingContext.sparkContext.setLogLevel("INFO")
        streamingContext.start()
        streamingContext.awaitTermination()
    }

}