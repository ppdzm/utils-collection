package io.github.ppdzm.utils.hadoop.scala.kafka

import io.github.ppdzm.utils.hadoop.scala.constants.KafkaConfigConstants
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2021/2/25.
 */
class KafkaUtilsTest extends FunSuite with KafkaConfigConstants {
    override protected val config: Config = new FileConfig()
    test("get topic list") {
        KafkaUtils.getTopicList(KAFKA_BROKERS.stringValue).foreach(println)
    }

    test("get partition number") {
        val kafkaBrokers = (KAFKA_BROKERS.stringValue)
        KafkaUtils.getTopicList(kafkaBrokers)
            .foreach {
                topic =>
                    val partitionNumber = KafkaUtils.getPartitionNumber(kafkaBrokers, topic)
                    println(s"partition number of topic $topic is " + partitionNumber)
            }
    }
}
