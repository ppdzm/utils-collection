package io.github.ppdzm.utils.hadoop.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * Created by Stuart Alex on 2017/3/29.
 */
public class KafkaConsumerProperties {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends PropertiesBuilder<ConsumerConfig> {

        public <K, V> KafkaConsumer<K, V> newConsumer() {
            return new KafkaConsumer<>(build());
        }

        /**
         * The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if <code>enable.auto.commit</code> is set to <code>true</code>.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder AUTO_COMMIT_INTERVAL_MS(Object value) {
            properties.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, value);
            return this;
        }

        /**
         * What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
         * 1.      earliest: automatically reset the offset to the earliest offset
         * 2.        latest: automatically reset the offset to the latest offset
         * 3.          none: throw exception to the consumer if no previous offset is found for the consumer's group
         * 4. anything else: throw exception to the consumer.
         *
         * @param strategy offset重设策略
         * @return {@link Builder}
         */
        public Builder AUTO_OFFSET_RESET(OffsetResetStrategy strategy) {
            properties.put(AUTO_OFFSET_RESET_CONFIG, strategy.toString().toLowerCase());
            return this;
        }

        /**
         * kafka brokers' address (with port)
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder BOOTSTRAP_SERVERS(Object value) {
            properties.put(BOOTSTRAP_SERVERS_CONFIG, value);
            return this;
        }

        /**
         * Automatically check the CRC32 of the records consumed.
         * return this; ensures no on-the-wire or on-disk corruption to the messages occurred.
         * return this; check adds some overhead, so it may be disabled in cases seeking extreme performance.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder CHECK_CRCS(Object value) {
            properties.put(CHECK_CRCS_CONFIG, value);
            return this;
        }

        public Builder CLIENT_ID(Object value) {
            properties.put(CLIENT_ID_CONFIG, value);
            return this;
        }

        public Builder CONNECTIONS_MAX_IDLE_MS(Object value) {
            properties.put(CONNECTIONS_MAX_IDLE_MS_CONFIG, value);
            return this;
        }

        /**
         * Specifies the timeout (in milliseconds) for consumer APIs that could block.
         * return this; configuration is used as the default timeout for all consumer operations that do not explicitly accept a <code>timeout</code> parameter.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder DEFAULT_API_TIMEOUT_MS(Object value) {
            properties.put(DEFAULT_API_TIMEOUT_MS_CONFIG, value);
            return this;
        }

        /**
         * If true the consumer's offset will be periodically committed in the background.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder ENABLE_AUTO_COMMIT(boolean value) {
            properties.put(ENABLE_AUTO_COMMIT_CONFIG, value);
            return this;
        }

        /**
         * Whether internal topics matching a subscribed pattern should be excluded from the subscription. It is always possible to explicitly subscribe to an internal topic.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder EXCLUDE_INTERNAL_TOPICS(Object value) {
            properties.put(EXCLUDE_INTERNAL_TOPICS_CONFIG, value);
            return this;
        }

        /**
         * The maximum amount of data the server should return for a fetch request.
         * Records are fetched in batches by the consumer, and if the first record batch in the first non-empty partition of the fetch is larger than return this; value, the record batch will still be returned to ensure that the consumer can make progress.
         * As such, return this; is not a absolute maximum. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).
         * Note that the consumer performs multiple fetches in parallel.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder FETCH_MAX_BYTES(Object value) {
            properties.put(FETCH_MAX_BYTES_CONFIG, value);
            return this;
        }

        /**
         * The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately satisfy the requirement given by fetch.min.bytes.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder FETCH_MAX_WAIT_MS(Object value) {
            properties.put(FETCH_MAX_WAIT_MS_CONFIG, value);
            return this;
        }

        /**
         * The minimum amount of data the server should return for a fetch request.
         * If insufficient data is available the request will wait for that much data to accumulate before answering the request.
         * The default setting of 1 byte means that fetch requests are answered as soon as a single byte of data is available or the fetch request times out waiting for data to arrive.
         * Setting return this; to something greater than 1 will cause the server to wait for larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional latency.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder FETCH_MIN_BYTES(Object value) {
            properties.put(FETCH_MIN_BYTES_CONFIG, value);
            return this;
        }

        /**
         * A unique string that identifies the consumer group return this; consumer belongs to.
         * return this; property is required if the consumer uses either the group management functionality by using <code>subscribe(topic)</code> or the Kafka-based offset management strategy.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder GROUP_ID(Object value) {
            properties.put(GROUP_ID_CONFIG, value);
            return this;
        }

        /**
         * The expected time between heartbeats to the consumer coordinator when using Kafka's group management facilities.
         * Heartbeats are used to ensure that the consumer's session stays active and to facilitate re-balancing when new consumers join or leave the group.
         * The value must be set lower than <code>session.timeout.ms</code>, but typically should be set no higher than 1/3 of that value.
         * It can be adjusted even lower to control the expected time for normal re-balances.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder HEARTBEAT_INTERVAL_MS(Object value) {
            properties.put(HEARTBEAT_INTERVAL_MS_CONFIG, value);
            return this;
        }

        /**
         * A list of classes to use as interceptors.
         * Implementing the [[org.apache.kafka.clients.consumer.ConsumerInterceptor]] interface allows you to intercept (and possibly mutate) records received by the consumer.
         * By default, there are no interceptors.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder INTERCEPTOR_CLASSES(Object value) {
            properties.put(INTERCEPTOR_CLASSES_CONFIG, value);
            return this;
        }

        /**
         * Controls how to read messages written transactionally.
         * If set to <code>read_committed</code>, consumer.poll() will only return transactional messages which have been committed.
         * If set to <code>read_uncommitted</code>' (the default), consumer.poll() will return all messages, even transactional messages which have been aborted.
         * Non-transactional messages will be returned unconditionally in either mode.
         * Messages will always be returned in offset order.
         * Hence, in <code>read_committed</code> mode, consumer.poll() will only return messages up to the last stable offset (LSO), which is the one less than the offset of the first open transaction.
         * In particular any messages appearing after messages belonging to ongoing transactions will be withheld until the relevant transaction has been completed.
         * As a result, <code>read_committed</code> consumers will not be able to read up to the high watermark when there are in flight transactions.
         * Further, when in <code>read_committed</code> the seekToEnd method will return the LSO.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder ISOLATION_LEVEL(Object value) {
            properties.put(ISOLATION_LEVEL_CONFIG, value);
            return this;
        }

        /**
         * Deserializer class for key that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.
         *
         * @param cls 类名
         * @return {@link Builder}
         */
        public Builder KEY_DESERIALIZER_CLASS(String cls) {
            properties.put(KEY_DESERIALIZER_CLASS_CONFIG, cls);
            return this;
        }

        /**
         * The maximum amount of data per-partition the server will return.
         * Records are fetched in batches by the consumer.
         * If the first record batch in the first non-empty partition of the fetch is larger than return this; limit, the batch will still be returned to ensure that the consumer can make progress.
         * The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).
         * See fetch.max.bytes for limiting the consumer request size.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder MAX_PARTITION_FETCH_BYTES(Object value) {
            properties.put(MAX_PARTITION_FETCH_BYTES_CONFIG, value);
            return this;
        }

        /**
         * The maximum delay between invocations of poll() when using consumer group management.
         * return this; places an upper bound on the amount of time that the consumer can be idle before fetching more records.
         * If poll() is not called before expiration of return this; timeout, then the consumer is considered failed and the group will re-balance in order to reassign the partitions to another member.
         * For consumers using a non-null <code>group.instance.id</code> which reach return this; timeout, partitions will not be immediately reassigned.
         * Instead, the consumer will stop sending heartbeats and partitions will be reassigned after expiration of <code>session.timeout.ms</code>.
         * return this; mirrors the behavior of a static consumer which has shutdown.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder MAX_POLL_INTERVAL_MS(Object value) {
            properties.put(MAX_POLL_INTERVAL_MS_CONFIG, value);
            return this;
        }

        /**
         * The maximum number of records returned in a single call to poll().
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder MAX_POLL_RECORDS(Object value) {
            properties.put(MAX_POLL_RECORDS_CONFIG, value);
            return this;
        }

        public Builder METADATA_MAX_AGE(Object value) {
            properties.put(METADATA_MAX_AGE_CONFIG, value);
            return this;
        }

        public Builder METRICS_NUM_SAMPLES(Object value) {
            properties.put(METRICS_NUM_SAMPLES_CONFIG, value);
            return this;
        }

        public Builder METRICS_RECORDING_LEVEL(Object value) {
            properties.put(METRICS_RECORDING_LEVEL_CONFIG, value);
            return this;
        }

        public Builder METRICS_SAMPLE_WINDOW_MS(Object value) {
            properties.put(METRICS_SAMPLE_WINDOW_MS_CONFIG, value);
            return this;
        }

        public Builder METRIC_REPORTER_CLASSES(Object value) {
            properties.put(METRIC_REPORTER_CLASSES_CONFIG, value);
            return this;
        }

        /**
         * A list of class names or class types, ordered by preference, of supported assignors responsible for the partition assignment strategy that the client will use to distribute partition ownership amongst consumer instances when group management is used.
         * Implementing the [[org.apache.kafka.clients.consumer.ConsumerPartitionAssignor]] interface allows you to plug in a custom assignment strategy.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder PARTITION_ASSIGNMENT_STRATEGY(Object value) {
            properties.put(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, value);
            return this;
        }

        public Builder RECEIVE_BUFFER(Object value) {
            properties.put(RECEIVE_BUFFER_CONFIG, value);
            return this;
        }

        public Builder RECONNECT_BACKOFF_MAX_MS(Object value) {
            properties.put(RECONNECT_BACKOFF_MAX_MS_CONFIG, value);
            return this;
        }

        public Builder RECONNECT_BACKOFF_MS(Object value) {
            properties.put(RECONNECT_BACKOFF_MS_CONFIG, value);
            return this;
        }

        /**
         * The configuration controls the maximum amount of time the client will wait for the response of a request.
         * If the response is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries are exhausted.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder REQUEST_TIMEOUT_MS(Object value) {
            properties.put(REQUEST_TIMEOUT_MS_CONFIG, value);
            return this;
        }

        public Builder RETRY_BACKOFF_MS(Object value) {
            properties.put(RETRY_BACKOFF_MS_CONFIG, value);
            return this;
        }

        public Builder SEND_BUFFER(Object value) {
            properties.put(SEND_BUFFER_CONFIG, value);
            return this;
        }

        /**
         * The timeout used to detect client failures when using Kafka's group management facility.
         * The client sends periodic heartbeats to indicate its liveness to the broker.
         * If no heartbeats are received by the broker before the expiration of return this; session timeout, then the broker will remove return this; client from the group and initiate a re-balance.
         * Note that the value must be in the allowable range as configured in the broker configuration by <code>group.min.session.timeout.ms</code> and <code>group.max.session.timeout.ms</code>.
         *
         * @param value 值
         * @return {@link Builder}
         */
        public Builder SESSION_TIMEOUT_MS(Object value) {
            properties.put(SESSION_TIMEOUT_MS_CONFIG, value);
            return this;
        }

        /**
         * Deserializer class for value that implements the [[org.apache.kafka.common.serialization.Deserializer]] interface.
         *
         * @param cls 类名
         * @return {@link Builder}
         */
        public Builder VALUE_DESERIALIZER_CLASS(String cls) {
            properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, cls);
            return this;
        }

    }
}