package io.github.ppdzm.utils.hadoop.scala.kafka.producer

import io.github.ppdzm.utils.hadoop.scala.kafka.config.KafkaProducerProperties.Builder
import io.github.ppdzm.utils.hadoop.scala.kafka.config.{KafkaConsumerProperties, KafkaProducerProperties}
import io.github.ppdzm.utils.hadoop.scala.kafka.functions._
import io.github.ppdzm.utils.universal.base.{Mathematics, ResourceUtils, StringUtils}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetResetStrategy}
import org.apache.kafka.clients.producer.KafkaProducer
import scalikejdbc.LoanPattern

import java.io.File
import java.nio.file.NotDirectoryException
import java.time.Duration
import java.util
import java.util.Properties
import scala.io.Source
import scala.reflect.ClassTag

/**
 * Created by Stuart Alex on 2021/2/25.
 */
abstract class EasyKafkaProducer[KS: ClassTag, VS: ClassTag, T] {
    protected lazy val kafkaProducer = new KafkaProducer[String, T](producerProperties)
    protected var producerProperties: Properties = _
    protected var destinationTopic: String = _

    /**
     * 以文件夹下的文件内容为数据源（每个文件作为一条数据）
     *
     * @param directory     文件夹
     * @param fileNameRegex 文件名正则
     * @return
     */
    def fromDirectoryFileContent(directory: String, fileNameRegex: String = ".*"): DataWrapper = {
        val url = ResourceUtils.locateResourceAsUrl(directory)
        val data = new File(url.getPath).listFiles().filter(_.getName.matches(fileNameRegex))
          .map { file => LoanPattern.using(Source.fromFile(file, "utf-8"))(s => s.getLines().mkString) }
        new DataWrapper(data)
    }

    /**
     * 以文件夹下的文件内容为数据源（每个文件中的一行作为一条数据）
     *
     * @param directory     文件夹
     * @param fileNameRegex 文件名正则
     * @return
     */
    def fromDirectoryFileLines(directory: String, fileNameRegex: String = ".*"): DataWrapper = {
        val url = ResourceUtils.locateResourceAsUrl(directory)
        if (url == null)
            throw new NotDirectoryException(directory)
        val data = new File(url.getPath).listFiles().filter(_.getName.matches(fileNameRegex))
          .flatMap { file => LoanPattern.using(Source.fromFile(file, "utf-8"))(s => s.getLines().toList) }
        new DataWrapper(data)
    }

    /**
     * 以文件内容为数据源（文件中的一行作为一条数据）
     *
     * @param fileName 文件名
     * @return
     */
    def fromFileLines(fileName: String): DataWrapper = {
        val inputStream = ResourceUtils.locateAsInputStream(fileName)
        val data = LoanPattern.using(Source.fromInputStream(inputStream, "utf-8"))(s => s.getLines().toArray)
        new DataWrapper(data)
    }

    /**
     * 以Kafka某个topic为数据源
     *
     * @tparam K Class of Key
     * @tparam V Class of Value
     * @return
     */
    def fromKafka[K, V](fromTopics: Array[String]): KafkaSourceBuilder[K, V] = {
        new KafkaSourceBuilder[K, V](fromTopics, this)
    }

    /**
     * 以字符串数组为数据源
     *
     * @param data 字符串数组
     * @return
     */
    def fromStrings(data: Array[String]): DataWrapper = {
        new DataWrapper(data)
    }

    /**
     * 发送单个字符串数据
     *
     * @param datum 字符串数据
     */
    def send(datum: String): Unit

    private[EasyKafkaProducer] trait WithWrapper {
        protected var condition: Condition = AlwaysTrueCondition
        protected var exceptionHandler: ExceptionHandler = PrintExceptionHandler

        /**
         * 设置停止条件
         *
         * @param condition 停止条件
         * @return
         */
        def withCondition(condition: Condition): this.type = {
            this.condition = condition
            this
        }

        /**
         * 设置异常处理器
         *
         * @param exceptionHandler 异常处理器
         * @return
         */
        def withExceptionHandler(exceptionHandler: ExceptionHandler): this.type = {
            this.exceptionHandler = exceptionHandler
            this
        }
    }

    private[EasyKafkaProducer] trait KafkaWithWrapper[K, V] extends WithWrapper {
        protected val builder: Builder = KafkaProducerProperties.builder().KEY_SERIALIZER_CLASS[KS].VALUE_SERIALIZER_CLASS[VS]

        def withBrokers(brokers: String): this.type = {
            builder.BOOTSTRAP_SERVERS(brokers)
            this
        }

        def withExtraParameter(key: String, value: AnyRef): this.type = {
            builder.put(key, value)
            this
        }

        def withExtraParameters(extraParameters: Map[String, AnyRef]): this.type = {
            builder.invoke(extraParameters)
            this
        }
    }

    private[EasyKafkaProducer] class KafkaSourceBuilder[K, V](fromTopics: Array[String], producer: EasyKafkaProducer[KS, VS, T]) {
        private val builder = KafkaConsumerProperties.builder()

        def toKafka(toTopic: String): Kafka2KafkaSinkBuilder[K, V] = {
            val consumerProperties: Properties = builder.build()
            val kafkaConsumer = new KafkaConsumer[K, V](consumerProperties)
            kafkaConsumer.subscribe(util.Arrays.asList(fromTopics: _*))
            destinationTopic = toTopic
            new Kafka2KafkaSinkBuilder[K, V](kafkaConsumer, producer)
        }

        def withAutoCommit(autoCommit: Boolean): this.type = {
            builder.ENABLE_AUTO_COMMIT(autoCommit)
            this
        }

        def withBrokers(brokers: String): this.type = {
            builder.BOOTSTRAP_SERVERS(brokers)
            this
        }

        def withExtraParameters(extraParameters: Map[String, AnyRef]): this.type = {
            builder.invoke(extraParameters)
            this
        }

        def withGroupId(groupId: String = StringUtils.randomString(8)): this.type = {
            builder.GROUP_ID(groupId)
            this
        }

        def withKeyDeserializer[KD: ClassTag]: this.type = {
            builder.KEY_DESERIALIZER_CLASS[KD]
            this
        }

        def withOffsetResetStrategy(offsetResetStrategy: OffsetResetStrategy): this.type = {
            builder.AUTO_OFFSET_RESET(offsetResetStrategy)
            this
        }

        def withValueDeserializer[VD: ClassTag]: this.type = {
            builder.VALUE_DESERIALIZER_CLASS[VD]
            this
        }
    }

    private[EasyKafkaProducer] class Kafka2KafkaSinkBuilder[K, V](kafkaConsumer: KafkaConsumer[K, V], producer: EasyKafkaProducer[KS, VS, T]) extends KafkaWithWrapper[K, V] {
        protected var duration: Duration = Duration.ofSeconds(1)

        /**
         * 设置取数间隔
         *
         * @param duration 间隔
         * @return
         */
        def withDuration(duration: Duration): this.type = {
            this.duration = duration
            this
        }

        def startWith(recordProcessFunction: (ConsumerRecords[K, V], EasyKafkaProducer[KS, VS, T]) => Unit): Unit = {
            producerProperties = builder.build()
            while (condition.isTrue) {
                val records = kafkaConsumer.poll(duration)
                recordProcessFunction(records, producer)
            }
        }

    }

    private[EasyKafkaProducer] class Data2KafkaSinkBuilder[K, V](data: Array[String]) extends KafkaWithWrapper[K, V] {
        private var random = false
        private var substitutor: Substitutor = NoSubstitutor
        private var sleeper: Sleeper = NeverStopSleeper

        /**
         * 设置是否随机取数
         *
         * @param random 是否随机
         * @return
         */
        def withRandom(random: Boolean): this.type = {
            this.random = random
            this
        }

        /**
         * 设置文本替换器
         *
         * @param substitutor 文本替换器
         * @return
         */
        def withSubstitutor(substitutor: Substitutor): this.type = {
            this.substitutor = substitutor
            this
        }

        /**
         * 设置休眠间隔
         *
         * @param sleeper Sleeper
         * @return
         */
        def withSleeper(sleeper: Sleeper): this.type = {
            this.sleeper = sleeper
            this
        }

        def start(): Unit = {
            producerProperties = builder.build()
            var cursor = -1
            while (condition.isTrue && (random || cursor < data.length - 1)) {
                val datum = if (random)
                    data(Mathematics.randomInt(0, data.length - 1)).replaceIf(substitutor.placeholder.notNullAndEmpty, substitutor.placeholder, substitutor.getSubstitutor)
                else {
                    cursor += 1
                    data(cursor).replaceIf(substitutor.placeholder.notNullAndEmpty, substitutor.placeholder, substitutor.getSubstitutor)
                }
                try {
                    send(datum)
                } catch {
                    case e: Exception => exceptionHandler.handle(e, datum)
                }
                sleeper.sleep()
            }
        }
    }

    private[EasyKafkaProducer] class DataWrapper(data: Array[String]) {
        /**
         * 设置目标topic
         *
         * @param toTopic 目标topic
         * @return
         */
        def toKafka(toTopic: String): Data2KafkaSinkBuilder[String, String] = {
            destinationTopic = toTopic
            new Data2KafkaSinkBuilder[String, String](data)
        }
    }

}
