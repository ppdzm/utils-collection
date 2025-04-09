package io.github.ppdzm.utils.spark.streaming.structured.reader

import io.github.ppdzm.utils.spark.streaming.structured.source.InputSources.kafkaSource
import io.github.ppdzm.utils.spark.streaming.structured.source.{Source, SourceOption}
import org.apache.spark.sql.{DataFrame, SparkSession}

class KafkaStreamReader(sparkSession: SparkSession, brokers: String)
    extends StreamReader(sparkSession: SparkSession) {
    inputSource(kafkaSource)
        .option(kafkaSource.options.`kafka.bootstrap.servers`, brokers)

    /**
     * Specific TopicPartitions to consume.
     * Only one of "assign", "subscribe" or "subscribePattern" options can be specified for Kafka source.
     *
     * @param json json string，example —— {"topicA":[0,1],"topicB":[2,4]}
     * @return
     */
    def assign(json: String): this.type = {
        option(kafkaSource.options.assign, json)
    }

    /**
     * The topic list to subscribe.
     * Only one of "assign", "subscribe" or "subscribePattern" options can be specified for Kafka source.
     *
     * @param topics A comma-separated list of topics
     * @return
     */
    def subscribe(topics: String): this.type = {
        option(kafkaSource.options.subscribe, topics)
    }

    /**
     * The pattern used to subscribe to topic(s).
     * Only one of "assign, "subscribe" or "subscribePattern" options can be specified for Kafka source.
     *
     * @param regex Java regex string of topic(s)
     * @return
     */
    def subscribePattern(regex: String): this.type = {
        option(kafkaSource.options.subscribePattern, regex)
    }

}
