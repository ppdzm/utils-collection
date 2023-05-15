package io.github.ppdzm.utils.flink.streaming.kafka.utils;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchemaBuilder;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

/**
 * @author Created by Stuart Alex on 2023/5/12.
 */
public class FlinkKafkaUtils {

    public static KafkaSink<ProducerRecord<String, String>> generateKafkaSink(String brokers, String topic, Properties additionalParams) {
        return generateKafkaSink(brokers, topic, additionalParams, null);
    }

    public static KafkaSink<ProducerRecord<String, String>> generateKafkaSink(String brokers, String topic, Properties additionalParams, FlinkKafkaPartitioner<ProducerRecord<String, String>> partitioner) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, brokers);
        if (additionalParams != null) {
            for (Object key : additionalParams.keySet()) {
                Object value = additionalParams.get(key);
                properties.put(key, value);
            }
        }
        final KafkaRecordSerializationSchemaBuilder<ProducerRecord<String, String>> builder = KafkaRecordSerializationSchema.builder()
                .setTopic(topic)
                .setValueSerializationSchema((SerializationSchema<ProducerRecord<String, String>>) producerRecord -> producerRecord.value().getBytes(StandardCharsets.UTF_8))
                .setKeySerializationSchema((SerializationSchema<ProducerRecord<String, String>>) producerRecord -> producerRecord.key().getBytes(StandardCharsets.UTF_8));
        if (partitioner != null) {
            builder.setPartitioner(partitioner);
        }
        return KafkaSink.<ProducerRecord<String, String>>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(builder.build())
                .setKafkaProducerConfig(properties)
                .build();
    }

}
