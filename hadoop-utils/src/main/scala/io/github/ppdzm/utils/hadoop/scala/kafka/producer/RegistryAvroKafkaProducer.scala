package io.github.ppdzm.utils.hadoop.scala.kafka.producer

import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.github.ppdzm.utils.universal.formats.avro.AvroUtils
import org.apache.avro.Schema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.collection.JavaConversions._

/**
 * Created by Stuart Alex on 2021/2/25.
 */
case class RegistryAvroKafkaProducer[T](schema: Schema) extends EasyKafkaProducer[StringSerializer, KafkaAvroSerializer, T] {

    /**
     * 发送单个字符串数据
     *
     * @param datum 字符串数据
     */
    override def send(datum: String): Unit = {
        AvroUtils
            .json2Avro[T](datum, schema)
            .foreach {
                avro =>
                    val producerRecord = new ProducerRecord[String, T](destinationTopic, avro)
                    kafkaProducer.send(producerRecord)
            }
    }
}