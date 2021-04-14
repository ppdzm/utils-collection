package io.github.ppdzm.utils.spark.streaming.structured.source

trait Source {
    override def toString: String = {
        this.getClass.getSimpleName.toLowerCase.replace("source$", "")
    }
}

trait SourceOptions

trait SourceOption {

    override def toString: String = {
        this.getClass.getName.replace("""$u002E""", ".").split("\\$").last
    }

}