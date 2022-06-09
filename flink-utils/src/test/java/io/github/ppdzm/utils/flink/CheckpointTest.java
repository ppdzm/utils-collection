package io.github.ppdzm.utils.flink;

import io.github.ppdzm.utils.flink.common.SimpleCheckpointConfiguration;
import io.github.ppdzm.utils.flink.streaming.kafka.FlinkKafkaStreaming;
import io.github.ppdzm.utils.universal.alert.LoggerAlerter;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Created by Stuart Alex on 2021/5/10.
 */
public class CheckpointTest {
    public static void main(String[] args) throws Exception {
        FlinkKafkaStreaming<String> kafkaStreaming = new FlinkKafkaStreaming<String>(
            "checkpoint-test",
            new LoggerAlerter(),
            new SimpleCheckpointConfiguration(),
            "10.25.21.41:9092,10.25.21.42:9092,10.25.21.43:9092",
            Collections.singletonList("stuart_test"),
            OffsetResetStrategy.EARLIEST,
            true,
            "checkpoint-test",
            null
        ) {
            private static final long serialVersionUID = -3323381426413599979L;

            @Override
            public void execute(DataStreamSource<String> dataStreamSource) {
                FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(getKafkaSourceBrokers(), "stuart_sink", new SimpleStringSchema());
                dataStreamSource
                    .addSink(producer);
            }
        };
        kafkaStreaming.start(new SimpleStringSchema());
    }
}
