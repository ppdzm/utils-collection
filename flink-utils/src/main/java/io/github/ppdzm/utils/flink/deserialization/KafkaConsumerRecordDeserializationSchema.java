package io.github.ppdzm.utils.flink.deserialization;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Created by Stuart Alex on 2021/6/15.
 */
public class KafkaConsumerRecordDeserializationSchema implements KafkaDeserializationSchema<ConsumerRecord<String, String>> {
    private static final long serialVersionUID = -5095416451110247795L;

    @Override
    public boolean isEndOfStream(ConsumerRecord<String, String> stringStringConsumerRecord) {
        return false;
    }

    @Override
    public ConsumerRecord<String, String> deserialize(ConsumerRecord<byte[], byte[]> record) {
        String key = "", value = "";
        if (record.key() != null) {
            key = new String(record.key());
        }
        if (record.value() != null) {
            value = new String(record.value());
        }
        return new ConsumerRecord<>(
            record.topic(),
            record.partition(),
            record.offset(),
            record.timestamp(),
            record.timestampType(),
            record.checksum(),
            record.serializedKeySize(),
            record.serializedValueSize(),
            key,
            value
        );
    }

    @Override
    public TypeInformation<ConsumerRecord<String, String>> getProducedType() {
        return TypeInformation.of(new TypeHint<ConsumerRecord<String, String>>() {
        });
    }
}
