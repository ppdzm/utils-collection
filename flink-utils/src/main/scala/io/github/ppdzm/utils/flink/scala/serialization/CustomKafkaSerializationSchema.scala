package io.github.ppdzm.utils.flink.scala.serialization

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

import java.lang

case class CustomKafkaSerializationSchema[T](topic: String) extends KafkaSerializationSchema[T] {
    override def serialize(element: T, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
        new ProducerRecord[Array[Byte], Array[Byte]](topic, null, element.toString.getBytes("utf-8"))
    }
}
