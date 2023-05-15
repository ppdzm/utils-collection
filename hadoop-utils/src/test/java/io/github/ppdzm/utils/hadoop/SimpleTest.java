package io.github.ppdzm.utils.hadoop;

import io.github.ppdzm.utils.hadoop.kafka.config.KafkaProducerProperties;

import java.util.Properties;

public class SimpleTest {
    public static void main(String[] args) {
        Properties properties = KafkaProducerProperties
                .builder()
                .ACKS(-1)
                .build();
    }
}
