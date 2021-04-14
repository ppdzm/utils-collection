package io.github.ppdzm.utils.hadoop.kafka

import java.io.File

import com.fasterxml.jackson.databind.node.ArrayNode
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import io.github.ppdzm.utils.universal.formats.json.JsonUtils
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.serialization.StringDeserializer
import org.sa.utils.hadoop.constants.KafkaConfigConstants
import org.sa.utils.hadoop.kafka.producer.RegistryAvroKafkaProducer
import org.sa.utils.universal.config.{Config, FileConfig}
import org.scalatest.FunSuite

import scala.collection.JavaConversions._

class KafkaIntegrationTest extends FunSuite with KafkaConfigConstants {
    override protected val config: Config = FileConfig()

    test("consumer json-string from kafka then producer to kafka with avro format") {
        val schemaRegistryUrl = config.newConfigItem("kafka.schemaRegistry.hosts").stringValue
        val sourceTopic = config.newConfigItem("kafka.src.topic").arrayValue()
        val destinationTopic = config.newConfigItem("kafka.dst.topic").stringValue
        val schema = new Schema.Parser().parse(new File("../data/json/schema-all.json"))
        RegistryAvroKafkaProducer[GenericData.Array[_]](schema)
            .fromKafka[String, String](sourceTopic)
            .withAutoCommit(true)
            .withBrokers(KAFKA_BROKERS.stringValue)
            .withGroupId(KAFKA_GROUP_ID.stringValue)
            .withKeyDeserializer[StringDeserializer]
            .withOffsetResetStrategy(OffsetResetStrategy.EARLIEST)
            .withValueDeserializer[StringDeserializer]
            .toKafka(destinationTopic)
            .withBrokers(KAFKA_BROKERS.stringValue)
            .withExtraParameter(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
            .startWith {
                case (records, producer) =>
                    records.map(_.value())
                        .map {
                            value =>
                                JsonUtils.parse(value)
                                    .asInstanceOf[ArrayNode]
                                    .map { node => s"""{"wechat.wechat":${JsonUtils.serialize(node)}}""" }
                                    .mkString("[", ",", "]")
                        }
                        .foreach {
                            value =>
                                producer.send(value)
                        }
            }
    }

}
