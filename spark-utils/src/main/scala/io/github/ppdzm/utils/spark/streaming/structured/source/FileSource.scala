package io.github.ppdzm.utils.spark.streaming.structured.source

private[source] object FileSource extends Source {

    object options extends SourceOptions {

        object path extends SourceOption

        object maxFilesPerTrigger extends SourceOption

        object latestFirst extends SourceOption {

            object Values {
                val default = false
                val `false` = false
                val `true` = true
            }

        }

    }

}
