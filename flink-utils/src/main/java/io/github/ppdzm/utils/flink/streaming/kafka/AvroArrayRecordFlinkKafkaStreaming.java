package io.github.ppdzm.utils.flink.streaming.kafka;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

/**
 * @author Created by Stuart Alex on 2021/5/8.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AvroArrayRecordFlinkKafkaStreaming extends AvroFlinkKafkaStreaming<GenericData.Array<GenericRecord>> {
}
