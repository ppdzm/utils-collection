package io.github.ppdzm.utils.hadoop.kafka.factory

import java.util.Properties

import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}
import org.apache.kafka.clients.consumer.KafkaConsumer

/**
 * Created by Stuart Alex on 2017/3/29.
 */
case class KafkaConsumerFactory(properties: Properties) extends BasePooledObjectFactory[KafkaConsumer[String, String]] {

    override def create(): KafkaConsumer[String, String] = new KafkaConsumer[String, String](properties)

    override def destroyObject(p: PooledObject[KafkaConsumer[String, String]]): Unit = p.getObject.close()

    override def wrap(consumer: KafkaConsumer[String, String]): PooledObject[KafkaConsumer[String, String]] = new DefaultPooledObject[KafkaConsumer[String, String]](consumer)

}
