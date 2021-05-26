package io.github.ppdzm.utils.hadoop.kafka

import io.github.ppdzm.utils.hadoop.constants.KafkaConfigConstants
import io.github.ppdzm.utils.hadoop.kafka.functions.{CountCondition, ExitExceptionHandler, TimeSleeper}
import io.github.ppdzm.utils.hadoop.kafka.producer.SimpleKafkaProducer
import io.github.ppdzm.utils.universal.base.{Logging, StringUtils}
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/4/1.
 */
class KafkaSimpleProducerTest extends FunSuite with KafkaConfigConstants with Logging {
    override protected val config: Config = new FileConfig()

    test("kafka-pooled-producer") {
        val brokers = KAFKA_BROKERS.stringValue
        val topic = "stuart_test"
        val msg = s"""{"contacts":["sa"],"mtype":"test","mcontent":"${StringUtils.randomString(10)}"}"""
        println(1)
        SimpleKafkaProducer.send(brokers, topic, "1")
        println(2)
        SimpleKafkaProducer.send(brokers, topic, "2")
        println(3)
        SimpleKafkaProducer.fromStrings(Array("3")).toKafka(topic).withBrokers(brokers).start()
        println(4)
        SimpleKafkaProducer.fromStrings(Array("4")).toKafka(topic).withBrokers(brokers).start()
        println("done")
    }

    test("simple-producer") {
        val brokers = KAFKA_BROKERS.stringValue
        val toTopic = "data_buffer_uat_dc_sdk_dev_json"
        SimpleKafkaProducer
            .fromDirectoryFileLines("../data/json/wechat/")
            .toKafka(toTopic)
            .withBrokers(brokers)
            .withCondition(CountCondition(10))
            .withExceptionHandler(ExitExceptionHandler)
            .withSleeper(TimeSleeper(1000))
            //            .withSubstitutor(NumberSubstitutor("1cb00feb-456a-456c-85a7-b42e269b5d61"))
            .withRandom(true)
            .start()
    }

}
