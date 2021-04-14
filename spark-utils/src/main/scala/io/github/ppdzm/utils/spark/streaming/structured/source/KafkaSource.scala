package io.github.ppdzm.utils.spark.streaming.structured.source

private[source] object KafkaSource extends Source {

    object options extends SourceOptions {

        object assign extends SourceOption

        object subscribe extends SourceOption

        object subscribePattern extends SourceOption

        object `kafka.bootstrap.servers` extends SourceOption

        object startingOffsets extends SourceOption

        object endingOffsets extends SourceOption

        object failOnDataLoss extends SourceOption

        object `kafkaConsumer.pollTimeoutMs` extends SourceOption

        object `fetchOffset.numRetries` extends SourceOption

        object `fetchOffset.retryIntervalMs` extends SourceOption

        object maxOffsetsPerTrigger extends SourceOption

    }

}

