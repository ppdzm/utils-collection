package io.github.ppdzm.utils.hadoop.kafka

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.github.ppdzm.utils.hadoop.kafka.functions.{CountCondition, ExitExceptionHandler, NumberSubstitutor, TimeSleeper}
import io.github.ppdzm.utils.hadoop.kafka.producer.RegistryAvroKafkaProducer
import io.github.ppdzm.utils.universal.base.Logging
import io.github.ppdzm.utils.universal.config.{Config, ConfigTrait, FileConfig}
import io.github.ppdzm.utils.universal.formats.avro.AvroUtils
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2021/1/29.
 */
class KafkaRegistryAvroProducerTest extends FunSuite with ConfigTrait with Logging {
    override protected val config: Config = FileConfig()
    test("from-file-to-kafka") {
        val schemaRegistryUrl = config.newConfigItem("kafka.schemaRegistry.hosts").stringValue
        val destinationBrokers = config.newConfigItem("kafka.brokers").stringValue
        val destinationTopic = "data_buffer_uat_dc_sdk_dev"
        val schema = AvroUtils.getSchema("../data/json/schema-all.json")
        RegistryAvroKafkaProducer[AnyRef](schema)
            .fromDirectoryFileLines("../data/json/ios")
            .toKafka(destinationTopic)
            .withBrokers(destinationBrokers)
            .withCondition(CountCondition(10))
            .withExceptionHandler(ExitExceptionHandler)
            .withSleeper(TimeSleeper(10000))
            .withSubstitutor(NumberSubstitutor("57AAD570-D38B-46AF-91A2-3C6D70D2FAA7"))
            .withExtraParameter(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
            .withRandom(true)
            .start()
    }

}
