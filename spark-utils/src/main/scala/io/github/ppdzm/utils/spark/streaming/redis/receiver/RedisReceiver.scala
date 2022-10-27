package io.github.ppdzm.utils.spark.streaming.redis.receiver

import io.github.ppdzm.utils.spark.streaming.redis.RedisConfigConstants
import io.github.ppdzm.utils.spark.streaming.redis.wrapper.{JedisClusterWrapper, JedisSingletonWrapper, JedisWrapper}
import io.github.ppdzm.utils.universal.base.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}

import java.util
import scala.util.{Failure, Success, Try}

/**
 * Created by Stuart Alex on 2017/4/6.
 */
abstract class RedisReceiver(keySet: Set[String], storageLevel: StorageLevel) extends Receiver[(String, String)](storageLevel) with RedisConfigConstants {
    private val logging = new Logging(getClass)
    private val host = REDIS_HOST.stringValue
    private val port = REDIS_PORT.intValue
    private val timeout = REDIS_TIMEOUT.intValue
    private val clusterEnabled = REDIS_CLUSTER_ENABLED.booleanValue
    private val struct = REDIS_STRUCT.stringValue

    override def onStart(): Unit = {
        //    implicit val akkaSystem = ActorSystem()
        val t = if (this.clusterEnabled) this.getRedisClusterConnection else this.getRedisSingletonConnection
        t match {
            case Success(j) =>
                this.logging.logInfo(s"OnStart, Connecting to Redis ${this.struct} API")
                new Thread("Redis List Receiver") {
                    override def run() {
                        receive(j)
                    }
                }.start()
            case Failure(f) =>
                this.logging.logError("Could not connect", f.getCause.asInstanceOf[Exception])
                restart("Could not connect", f)
            case _ =>
        }
    }

    def receive(j: JedisWrapper): Unit = {
        try {
            this.logging.logInfo("Accepting messages from Redis")
            while (!isStopped()) {
                var allNull = true
                keySet.iterator.foreach(key => {
                    val value = getData(j, key)
                    if (value != null) {
                        allNull = false
                        store(key -> value)
                    }
                })
                if (allNull)
                    Thread.sleep(timeout)
            }
        }
        catch {
            case e: Exception =>
                this.logging.logError("Got this exception: ", e)
                e.printStackTrace()
                restart("Trying to connect again")
        }
        finally {
            this.logging.logInfo("The receiver has been stopped - Terminating Redis Connection")
            try {
                j.close()
            } catch {
                case e: Exception =>
                    this.logging.logError("error on close connection, ignoring", e)
            }
        }
    }

    private def getRedisSingletonConnection: Try[JedisWrapper] = {
        this.logging.logInfo("Redis host connection string: " + host + ":" + port)
        this.logging.logInfo("Creating Redis Connection")
        Try(new JedisSingletonWrapper(new Jedis(host, port)))
    }

    private def getRedisClusterConnection: Try[JedisWrapper] = {
        this.logging.logInfo("Redis cluster host connection string: " + host + ":" + port)
        this.logging.logInfo("Jedis will attempt to discover the remaining cluster nodes automatically")
        this.logging.logInfo("Creating RedisCluster Connection")
        val jedisClusterNodes = new util.HashSet[HostAndPort]()
        jedisClusterNodes.add(new HostAndPort(host, port))
        Try(new JedisClusterWrapper(new JedisCluster(jedisClusterNodes)))
    }

    def getData(j: JedisWrapper, key: String): String

    override def onStop(): Unit = {
        this.logging.logInfo("OnStop ...nothing to do!")
    }

}