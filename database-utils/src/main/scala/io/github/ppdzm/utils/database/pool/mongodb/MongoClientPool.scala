package io.github.ppdzm.utils.database.pool.mongodb

import com.mongodb.client.{MongoClient, MongoClients}
import com.mongodb.{ConnectionString, MongoClientSettings}
import io.github.ppdzm.utils.universal.base.Logging

import scala.collection.mutable

/**
 * Created by Stuart Alex on 2017/4/5.
 */
object MongoClientPool {
    private val logging = new Logging(getClass)
    private val _pool = mutable.Map[String, MongoClient]()
    sys.addShutdownHook {
        this._pool.values.foreach { pool => pool.close() }
    }

    def apply(uri: String): MongoClient = {
        this._pool.getOrElse(uri, {
            this.logging.logInfo(s"MongoClient $uri does not exists, create it and add it into RedisProducerPool")
            synchronized {
                val mongoClient = getMongoClient(uri)
                this._pool += uri -> mongoClient
                mongoClient
            }
        })
    }

    def getMongoClient(uri: String): MongoClient = {
        val connectionString = new ConnectionString(uri)
        val mongoClientSettings = MongoClientSettings.builder.applyConnectionString(connectionString).retryReads(true).retryWrites(true).build
        MongoClients.create(mongoClientSettings)
    }

    def close(): Unit = {
        _pool.values.foreach(_.close)
    }

}