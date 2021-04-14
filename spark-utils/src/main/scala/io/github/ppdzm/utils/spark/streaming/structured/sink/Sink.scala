package io.github.ppdzm.utils.spark.streaming.structured.sink

trait Sink {

    override def toString: String = {
        this.getClass.getSimpleName.toLowerCase.replace("sink$", "")
    }

}

trait SinkOptions

trait SinkOption {

    override def toString: String = {
        this.getClass.getName.replace("""$u002E""", ".").split("\\$").last
    }

}