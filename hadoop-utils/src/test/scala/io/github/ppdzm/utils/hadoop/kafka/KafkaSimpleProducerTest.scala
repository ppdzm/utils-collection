package io.github.ppdzm.utils.hadoop.kafka

import io.github.ppdzm.utils.hadoop.constants.KafkaConfigConstants
import io.github.ppdzm.utils.hadoop.kafka.config.KafkaProducerProperties
import io.github.ppdzm.utils.hadoop.kafka.producer.SimpleKafkaProducer
import io.github.ppdzm.utils.universal.base.{LoggingTrait, ResourceUtils, StringUtils}
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.config.{SaslConfigs, SslConfigs}
import org.apache.kafka.common.serialization.StringSerializer
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/4/1.
 */
class KafkaSimpleProducerTest extends FunSuite with KafkaConfigConstants with LoggingTrait {
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
        //        val brokers = KAFKA_BROKERS.stringValue
        val brokers = "115.28.131.59:9093,118.190.89.43:9093,47.104.157.230:9093"
        val toTopic = "dynamic-flume-consumer-test"
        for (elem <- Range(1, 10)) {
            SimpleKafkaProducer.send(
                brokers,
                toTopic,
                StringUtils.randomString(1, "ABC".toCharArray),
                StringUtils.randomString(10)
            )
        }

        //        SimpleKafkaProducer
        //            .fromDirectoryFileLines("../data/json/wechat/")
        //            .toKafka(toTopic)
        //            .withBrokers(brokers)
        //            .withCondition(CountCondition(10))
        //            .withExceptionHandler(ExitExceptionHandler)
        //            .withSleeper(TimeSleeper(1000))
        //            //            .withSubstitutor(NumberSubstitutor("1cb00feb-456a-456c-85a7-b42e269b5d61"))
        //            .withRandom(true)
        //            .start()
    }

    test("wan-producer") {
        // 设置SASL账号
        val saslMechanism = "PLAIN"
        val username = "alikafka_pre-cn-7mz26msjf001"
        val password = "5jMaWORGBstX94TPR9efIgoVqeLjfyZ3"
        var prefix = "org.apache.kafka.common.security.scram.ScramLoginModule"
        if ("PLAIN".equalsIgnoreCase(saslMechanism))
            prefix = "org.apache.kafka.common.security.plain.PlainLoginModule"
        val jaasConfig = String.format("%s required username=\"%s\" password=\"%s\";", prefix, username, password)
        val props = KafkaProducerProperties
            .builder()
            //设置接入点，请通过控制台获取对应Topic的接入点
            .BOOTSTRAP_SERVERS("115.28.131.59:9093,118.190.89.43:9093,47.104.157.230:9093")
            //Kafka消息的序列化方式
            .KEY_SERIALIZER_CLASS[StringSerializer]
            .VALUE_SERIALIZER_CLASS[StringSerializer]
            //请求的最长等待时间
            .MAX_BLOCK_MS(Long.box(30000))
            //设置客户端内部重试次数
            .RETRIES(5)
            //设置客户端内部重试间隔
            .RECONNECT_BACKOFF_MS(Long.box(3000))
            //设置SSL根证书的路径，请记得将XXX修改为自己的路径
            //与sasl路径类似，该文件也不能被打包到jar中
            .put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ResourceUtils.locateFile("ssl.truststore.location", true))
            //根证书store的密码，保持不变
            .put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient")
            //接入协议，目前支持使用SASL_SSL协议接入
            .put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
            .put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig)
            //SASL鉴权方式，保持不变
            .put(SaslConfigs.SASL_MECHANISM, saslMechanism)
            //hostname校验改成空
            .put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "")
            .build()
        //构造Producer对象，注意，该对象是线程安全的，一般来说，一个进程内一个Producer对象即可；
        //如果想提高性能，可以多构造几个对象，但不要太多，最好不要超过5个
        val producer = new KafkaProducer[String, String](props)
        //构造一个Kafka消息
        val topic = "dynamic-flume-consumer-test" //消息所属的Topic，请在控制台申请之后，填写在这里
        for (elem <- Range(1, 10)) {
            val key = StringUtils.randomString(2, "ABC".toCharArray)
            //消息的内容
            val value = StringUtils.randomString(10)
            producer.send(new ProducerRecord[String, String](topic, key, value))
        }
    }

}
