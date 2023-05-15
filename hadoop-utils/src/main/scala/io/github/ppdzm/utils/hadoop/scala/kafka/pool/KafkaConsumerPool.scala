package io.github.ppdzm.utils.hadoop.scala.kafka.pool

import io.github.ppdzm.utils.hadoop.scala.kafka.factory.KafkaConsumerFactory
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object KafkaConsumerPool extends Pool[KafkaConsumer[String, String]] {

    def apply(properties: Properties): ObjectPool[KafkaConsumer[String, String]] = {
        val key = getKey(properties)
        this._pool.getOrElse(key, {
            this.logging.logInfo(s"Consumer with key $key does not exists, create it and add it into KafkaConsumer Pool")
            synchronized[ObjectPool[KafkaConsumer[String, String]]] {
                val pool = new GenericObjectPool[KafkaConsumer[String, String]](KafkaConsumerFactory(properties))
                this._pool.put(key, pool)
                pool
            }
        })
    }

}
