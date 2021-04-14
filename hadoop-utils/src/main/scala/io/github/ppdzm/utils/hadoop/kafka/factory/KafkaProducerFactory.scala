package io.github.ppdzm.utils.hadoop.kafka.factory

import java.util.Properties

import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}
import org.apache.kafka.clients.producer.KafkaProducer

/**
 * Created by Stuart Alex on 2017/3/29.
 */
case class KafkaProducerFactory(properties: Properties) extends BasePooledObjectFactory[KafkaProducer[String, String]] {

    override def create(): KafkaProducer[String, String] = new KafkaProducer[String, String](properties)

    override def destroyObject(pool: PooledObject[KafkaProducer[String, String]]): Unit = {
        println("close " + pool.getObject)
        pool.getObject.close()
    }

    override def wrap(producer: KafkaProducer[String, String]) = new DefaultPooledObject[KafkaProducer[String, String]](producer)

}
