package io.github.ppdzm.utils.hadoop.kafka.pool

import java.util.Properties

import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.feature.Pool
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.sa.utils.hadoop.kafka.factory.KafkaConsumerFactory
import org.sa.utils.universal.base.Logging

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object KafkaConsumerPool extends Pool[KafkaConsumer[String, String]] with Logging {

    def apply(properties: Properties): ObjectPool[KafkaConsumer[String, String]] = {
        val key = getKey(properties)
        this._pool.getOrElse(key, {
            this.logInfo(s"Consumer with key $key does not exists, create it and add it into KafkaConsumer Pool")
            synchronized[ObjectPool[KafkaConsumer[String, String]]] {
                val pool = new GenericObjectPool[KafkaConsumer[String, String]](KafkaConsumerFactory(properties))
                this._pool.put(key, pool)
                pool
            }
        })
    }

}
