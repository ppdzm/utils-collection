package io.github.ppdzm.utils.spark.streaming.structured.source

private[source] object RateSource extends Source {

    object options extends SourceOptions {

        object rowsPerSecond extends SourceOption

        object rampUpTime extends SourceOption

        object numPartitions extends SourceOption

    }

}

