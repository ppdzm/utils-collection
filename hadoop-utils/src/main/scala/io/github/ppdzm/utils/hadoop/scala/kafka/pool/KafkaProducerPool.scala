package io.github.ppdzm.utils.hadoop.scala.kafka.pool

import io.github.ppdzm.utils.hadoop.scala.kafka.config.KafkaProducerProperties
import io.github.ppdzm.utils.hadoop.scala.kafka.factory.KafkaProducerFactory
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object KafkaProducerPool extends Pool[KafkaProducer[String, String]] {

    def apply(brokers: String): ObjectPool[KafkaProducer[String, String]] = {
        val properties = KafkaProducerProperties
          .builder()
          .BOOTSTRAP_SERVERS(brokers)
          .KEY_SERIALIZER_CLASS[StringSerializer]
          .VALUE_SERIALIZER_CLASS[StringSerializer]
          .RETRIES(new Integer(3))
          .BATCH_SIZE(new Integer(16384 * 10))
          .LINGER_MS(new Integer(1000 * 10))
          .build()
        KafkaProducerPool(properties)
    }

    def apply(properties: Properties): ObjectPool[KafkaProducer[String, String]] = {
        val key = getKey(properties)
        this._pool.getOrElse(key, {
            this.logging.logInfo(s"Producer with key $key does not exists, create it and add it into KafkaProducer Pool")
            synchronized[ObjectPool[KafkaProducer[String, String]]] {
                val pool = new GenericObjectPool[KafkaProducer[String, String]](KafkaProducerFactory(properties))
                this._pool.put(key, pool)
                pool
            }
        })
    }

}
