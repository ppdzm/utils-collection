package io.github.ppdzm.utils.hadoop.kafka.producer

import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.sa.utils.hadoop.kafka.pool.KafkaProducerPool

/**
 * Created by Stuart Alex on 2017/3/26.
 */
object SimpleKafkaProducer extends EasyKafkaProducer[StringSerializer, StringSerializer, String] {
    /**
     * 向kafka发送消息
     *
     * @param brokers  brokers列表
     * @param topic    Topic
     * @param messages 要发送的信息
     */
    def send(brokers: String, topic: String, messages: Seq[String]): Unit = {
        Pool.borrow(KafkaProducerPool(brokers)) {
            producer =>
                messages.foreach {
                    msg =>
                        producer.send(new ProducerRecord[String, String](topic, msg))
                }
        }
    }

    /**
     * 向kafka发送消息
     *
     * @param brokers brokers列表
     * @param topic   Topic
     * @param message 要发送的信息
     */
    def send(brokers: String, topic: String, message: String): Unit = {
        Pool.borrow(KafkaProducerPool(brokers)) { producer => producer.send(new ProducerRecord[String, String](topic, message)) }
    }

    /**
     * 向kafka发送消息
     *
     * @param brokers brokers列表
     * @param topic   Topic
     * @param key     key
     * @param message 要发送的信息
     */
    def send(brokers: String, topic: String, key: String, message: String): Unit = {
        Pool.borrow(KafkaProducerPool(brokers)) { producer => producer.send(new ProducerRecord[String, String](topic, key, message)) }
    }

    /**
     * 发送单个字符串数据
     *
     * @param datum 字符串数据
     */
    override def send(datum: String): Unit = {
        assert(destinationTopic.nonEmpty, "destination topic is not parameterized")
        val producerRecord = new ProducerRecord[String, String](destinationTopic, datum)
        kafkaProducer.send(producerRecord).get()
    }
}
