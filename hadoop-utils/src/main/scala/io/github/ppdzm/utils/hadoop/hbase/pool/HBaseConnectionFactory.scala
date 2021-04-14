package io.github.ppdzm.utils.hadoop.hbase.pool

import io.github.ppdzm.utils.hadoop.hbase.HBaseEnvironment
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}

/**
 * Created by Stuart Alex on 2017/4/5.
 */
private[pool] case class HBaseConnectionFactory(zookeeperQuorum: String, zookeeperPort: Int = 2181) extends BasePooledObjectFactory[Connection] with HBaseEnvironment {

    override def create(): Connection = {
        ConnectionFactory.createConnection(configuration)
    }

    override def wrap(connection: Connection) = new DefaultPooledObject[Connection](connection)

    override def validateObject(pool: PooledObject[Connection]): Boolean = !pool.getObject.isClosed

    override def destroyObject(pool: PooledObject[Connection]): Unit = pool.getObject.close()

    override def passivateObject(pool: PooledObject[Connection]): Unit = {}

}
