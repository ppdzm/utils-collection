package io.github.ppdzm.utils.hadoop.kafka.config

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetResetStrategy}

import scala.reflect._

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object KafkaConsumerProperties {

    def builder(): Builder = {
        new Builder()
    }

    private[KafkaConsumerProperties] class Builder extends PropertiesBuilder[ConsumerConfig] {

        import org.apache.kafka.clients.consumer.ConsumerConfig._

        def newConsumer[K, V]: KafkaConsumer[K, V] = {
            new KafkaConsumer[K, V](build())
        }

        /**
         * The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if <code>enable.auto.commit</code> is set to <code>true</code>.
         *
         * @param value
         * @return
         */
        def AUTO_COMMIT_INTERVAL_MS(value: AnyRef): this.type = {
            properties.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, value)
            this
        }

        /**
         * What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
         * 1.      earliest: automatically reset the offset to the earliest offset
         * 2.        latest: automatically reset the offset to the latest offset
         * 3.          none: throw exception to the consumer if no previous offset is found for the consumer's group
         * 4. anything else: throw exception to the consumer.
         *
         * @param strategy
         * @return
         */
        def AUTO_OFFSET_RESET(strategy: OffsetResetStrategy): this.type = {
            properties.put(AUTO_OFFSET_RESET_CONFIG, strategy.toString.toLowerCase)
            this
        }

        /**
         * kafka brokers' address (with port)
         *
         * @param value
         * @return
         */
        def BOOTSTRAP_SERVERS(value: AnyRef): this.type = {
            properties.put(BOOTSTRAP_SERVERS_CONFIG, value)
            this
        }

        /**
         * Automatically check the CRC32 of the records consumed.
         * This ensures no on-the-wire or on-disk corruption to the messages occurred.
         * This check adds some overhead, so it may be disabled in cases seeking extreme performance.
         *
         * @param value
         * @return
         */
        def CHECK_CRCS(value: AnyRef): this.type = {
            properties.put(CHECK_CRCS_CONFIG, value)
            this
        }

        def CLIENT_ID(value: AnyRef): this.type = {
            properties.put(CLIENT_ID_CONFIG, value)
            this
        }

        def CONNECTIONS_MAX_IDLE_MS(value: AnyRef): this.type = {
            properties.put(CONNECTIONS_MAX_IDLE_MS_CONFIG, value)
            this
        }

        /**
         * Specifies the timeout (in milliseconds) for consumer APIs that could block.
         * This configuration is used as the default timeout for all consumer operations that do not explicitly accept a <code>timeout</code> parameter.
         *
         * @param value
         * @return
         */
        def DEFAULT_API_TIMEOUT_MS(value: AnyRef): this.type = {
            properties.put(DEFAULT_API_TIMEOUT_MS_CONFIG, value)
            this
        }

        /**
         * If true the consumer's offset will be periodically committed in the background.
         *
         * @param value
         * @return
         */
        def ENABLE_AUTO_COMMIT(value: Boolean): this.type = {
            properties.put(ENABLE_AUTO_COMMIT_CONFIG, new java.lang.Boolean(value))
            this
        }

        /**
         * Whether internal topics matching a subscribed pattern should be excluded from the subscription. It is always possible to explicitly subscribe to an internal topic.
         *
         * @param value
         * @return
         */
        def EXCLUDE_INTERNAL_TOPICS(value: AnyRef): this.type = {
            properties.put(EXCLUDE_INTERNAL_TOPICS_CONFIG, value)
            this
        }

        /**
         * The maximum amount of data the server should return for a fetch request.
         * Records are fetched in batches by the consumer, and if the first record batch in the first non-empty partition of the fetch is larger than this value, the record batch will still be returned to ensure that the consumer can make progress.
         * As such, this is not a absolute maximum. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).
         * Note that the consumer performs multiple fetches in parallel.
         *
         * @param value
         * @return
         */
        def FETCH_MAX_BYTES(value: AnyRef): this.type = {
            properties.put(FETCH_MAX_BYTES_CONFIG, value)
            this
        }

        /**
         * The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately satisfy the requirement given by fetch.min.bytes.
         *
         * @param value
         * @return
         */
        def FETCH_MAX_WAIT_MS(value: AnyRef): this.type = {
            properties.put(FETCH_MAX_WAIT_MS_CONFIG, value)
            this
        }

        /**
         * The minimum amount of data the server should return for a fetch request.
         * If insufficient data is available the request will wait for that much data to accumulate before answering the request.
         * The default setting of 1 byte means that fetch requests are answered as soon as a single byte of data is available or the fetch request times out waiting for data to arrive.
         * Setting this to something greater than 1 will cause the server to wait for larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional latency.
         *
         * @param value
         * @return
         */
        def FETCH_MIN_BYTES(value: AnyRef): this.type = {
            properties.put(FETCH_MIN_BYTES_CONFIG, value)
            this
        }

        /**
         * A unique string that identifies the consumer group this consumer belongs to.
         * This property is required if the consumer uses either the group management functionality by using <code>subscribe(topic)</code> or the Kafka-based offset management strategy.
         *
         * @param value
         * @return
         */
        def GROUP_ID(value: AnyRef): this.type = {
            properties.put(GROUP_ID_CONFIG, value)
            this
        }

        /**
         * The expected time between heartbeats to the consumer coordinator when using Kafka's group management facilities.
         * Heartbeats are used to ensure that the consumer's session stays active and to facilitate rebalancing when new consumers join or leave the group.
         * The value must be set lower than <code>session.timeout.ms</code>, but typically should be set no higher than 1/3 of that value.
         * It can be adjusted even lower to control the expected time for normal rebalances.
         *
         * @param value
         * @return
         */
        def HEARTBEAT_INTERVAL_MS(value: AnyRef): this.type = {
            properties.put(HEARTBEAT_INTERVAL_MS_CONFIG, value)
            this
        }

        /**
         * A list of classes to use as interceptors.
         * Implementing the [[org.apache.kafka.clients.consumer.ConsumerInterceptor]] interface allows you to intercept (and possibly mutate) records received by the consumer.
         * By default, there are no interceptors.
         *
         * @param value
         * @return
         */
        def INTERCEPTOR_CLASSES(value: AnyRef): this.type = {
            properties.put(INTERCEPTOR_CLASSES_CONFIG, value)
            this
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
         * @param value
         * @return
         */
        def ISOLATION_LEVEL(value: AnyRef): this.type = {
            properties.put(ISOLATION_LEVEL_CONFIG, value)
            this
        }

        /**
         * Deserializer class for key that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.
         *
         * @tparam T
         * @return
         */
        def KEY_DESERIALIZER_CLASS[T: ClassTag]: this.type = {
            properties.put(KEY_DESERIALIZER_CLASS_CONFIG, classTag[T].runtimeClass)
            this
        }

        /**
         * The maximum amount of data per-partition the server will return.
         * Records are fetched in batches by the consumer.
         * If the first record batch in the first non-empty partition of the fetch is larger than this limit, the batch will still be returned to ensure that the consumer can make progress.
         * The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).
         * See fetch.max.bytes for limiting the consumer request size.
         *
         * @param value
         * @return
         */
        def MAX_PARTITION_FETCH_BYTES(value: AnyRef): this.type = {
            properties.put(MAX_PARTITION_FETCH_BYTES_CONFIG, value)
            this
        }

        /**
         * The maximum delay between invocations of poll() when using consumer group management.
         * This places an upper bound on the amount of time that the consumer can be idle before fetching more records.
         * If poll() is not called before expiration of this timeout, then the consumer is considered failed and the group will rebalance in order to reassign the partitions to another member.
         * For consumers using a non-null <code>group.instance.id</code> which reach this timeout, partitions will not be immediately reassigned.
         * Instead, the consumer will stop sending heartbeats and partitions will be reassigned after expiration of <code>session.timeout.ms</code>.
         * This mirrors the behavior of a static consumer which has shutdown.
         *
         * @param value
         * @return
         */
        def MAX_POLL_INTERVAL_MS(value: AnyRef): this.type = {
            properties.put(MAX_POLL_INTERVAL_MS_CONFIG, value)
            this
        }

        /**
         * The maximum number of records returned in a single call to poll().
         *
         * @param value
         * @return
         */
        def MAX_POLL_RECORDS(value: AnyRef): this.type = {
            properties.put(MAX_POLL_RECORDS_CONFIG, value)
            this
        }

        def METADATA_MAX_AGE(value: AnyRef): this.type = {
            properties.put(METADATA_MAX_AGE_CONFIG, value)
            this
        }

        def METRICS_NUM_SAMPLES(value: AnyRef): this.type = {
            properties.put(METRICS_NUM_SAMPLES_CONFIG, value)
            this
        }

        def METRICS_RECORDING_LEVEL(value: AnyRef): this.type = {
            properties.put(METRICS_RECORDING_LEVEL_CONFIG, value)
            this
        }

        def METRICS_SAMPLE_WINDOW_MS(value: AnyRef): this.type = {
            properties.put(METRICS_SAMPLE_WINDOW_MS_CONFIG, value)
            this
        }

        def METRIC_REPORTER_CLASSES(value: AnyRef): this.type = {
            properties.put(METRIC_REPORTER_CLASSES_CONFIG, value)
            this
        }

        /**
         * A list of class names or class types, ordered by preference, of supported assignors responsible for the partition assignment strategy that the client will use to distribute partition ownership amongst consumer instances when group management is used.
         * Implementing the [[org.apache.kafka.clients.consumer.ConsumerPartitionAssignor]] interface allows you to plug in a custom assignment strategy.
         *
         * @param value
         * @return
         */
        def PARTITION_ASSIGNMENT_STRATEGY(value: AnyRef): this.type = {
            properties.put(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, value)
            this
        }

        def RECEIVE_BUFFER(value: AnyRef): this.type = {
            properties.put(RECEIVE_BUFFER_CONFIG, value)
            this
        }

        def RECONNECT_BACKOFF_MAX_MS(value: AnyRef): this.type = {
            properties.put(RECONNECT_BACKOFF_MAX_MS_CONFIG, value)
            this
        }

        def RECONNECT_BACKOFF_MS(value: AnyRef): this.type = {
            properties.put(RECONNECT_BACKOFF_MS_CONFIG, value)
            this
        }

        /**
         * The configuration controls the maximum amount of time the client will wait for the response of a request.
         * If the response is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries are exhausted.
         *
         * @param value
         * @return
         */
        def REQUEST_TIMEOUT_MS(value: AnyRef): this.type = {
            properties.put(REQUEST_TIMEOUT_MS_CONFIG, value)
            this
        }

        def RETRY_BACKOFF_MS(value: AnyRef): this.type = {
            properties.put(RETRY_BACKOFF_MS_CONFIG, value)
            this
        }

        def SEND_BUFFER(value: AnyRef): this.type = {
            properties.put(SEND_BUFFER_CONFIG, value)
            this
        }

        /**
         * The timeout used to detect client failures when using Kafka's group management facility.
         * The client sends periodic heartbeats to indicate its liveness to the broker.
         * If no heartbeats are received by the broker before the expiration of this session timeout, then the broker will remove this client from the group and initiate a rebalance.
         * Note that the value must be in the allowable range as configured in the broker configuration by <code>group.min.session.timeout.ms</code> and <code>group.max.session.timeout.ms</code>.
         *
         * @param value
         * @return
         */
        def SESSION_TIMEOUT_MS(value: AnyRef): this.type = {
            properties.put(SESSION_TIMEOUT_MS_CONFIG, value)
            this
        }

        /**
         * Deserializer class for value that implements the [[org.apache.kafka.common.serialization.Deserializer]] interface.
         *
         * @tparam T
         * @return
         */
        def VALUE_DESERIALIZER_CLASS[T: ClassTag]: this.type = {
            properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, classTag[T].runtimeClass)
            this
        }

    }

}
