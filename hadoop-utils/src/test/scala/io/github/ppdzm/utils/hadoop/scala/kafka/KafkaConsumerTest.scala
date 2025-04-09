package io.github.ppdzm.utils.hadoop.scala.kafka

import io.github.ppdzm.utils.hadoop.kafka.config.KafkaConsumerProperties
import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetResetStrategy}
import org.scalatest.FunSuite

import java.time.Duration
import java.util.Collections

class KafkaConsumerTest extends FunSuite {

    test("consumer-test") {
        val consumer = new KafkaConsumer[String, String](
            KafkaConsumerProperties
              .builder
              .BOOTSTRAP_SERVERS("172.16.51.229:9092,172.16.175.205:9092,172.16.113.93:9092")
              .KEY_DESERIALIZER_CLASS("org.apache.kafka.common.serialization.StringDeserializer")
              .VALUE_DESERIALIZER_CLASS("org.apache.kafka.common.serialization.StringDeserializer")
              .GROUP_ID("kafka-test")
              .AUTO_OFFSET_RESET(OffsetResetStrategy.EARLIEST)
              .build
        )
        consumer.subscribe(Collections.singleton("useful_user_log"))
        val data = consumer.poll(Duration.ofSeconds(10))
        println(data)
    }

}
