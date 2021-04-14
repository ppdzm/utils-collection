package io.github.ppdzm.utils.hadoop.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.sa.utils.hadoop.hbase.pool.HBaseConnectionPool

trait HBaseEnvironment {
    protected lazy val configuration: Configuration = {
        val conf = HBaseConfiguration.create()
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum)
        conf.setInt("hbase.zookeeper.property.clientPort", zookeeperPort)
        conf
    }
    protected val zookeeperQuorum: String
    protected val zookeeperPort: Int

    def connection: Connection = HBaseConnectionPool(zookeeperQuorum, zookeeperPort).borrowObject()

    def close(): Unit = HBaseConnectionPool(zookeeperQuorum, zookeeperPort).close()
}
