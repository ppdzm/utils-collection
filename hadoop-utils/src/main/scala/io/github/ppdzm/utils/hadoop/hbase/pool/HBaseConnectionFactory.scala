package io.github.ppdzm.utils.hadoop.hbase.pool

import io.github.ppdzm.utils.hadoop.hbase.HBaseEnvironment
import io.github.ppdzm.utils.hadoop.security.KerberosConfig
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}
import org.apache.hadoop.hbase.client.Connection

/**
 * Created by Stuart Alex on 2017/4/5.
 */
private[pool] case class HBaseConnectionFactory(zookeeperQuorum: String, zookeeperPort: Int = 2181, kerberosEnabled: Boolean = false, kerberosConfig: KerberosConfig = null) extends BasePooledObjectFactory[Connection] with HBaseEnvironment {

    override def create(): Connection = {
        //        configuration.set("hbase.zookeeper.quorum", zookeeperQuorum)
        //        configuration.setInt("hbase.zookeeper.property.clientPort", zookeeperPort)
        //        if (kerberosEnabled) {
        //            System.setProperty("java.security.krb5.realm", kerberosConfig.realm)
        //            System.setProperty("java.security.krb5.kdc", kerberosConfig.kdcHost)
        //            System.setProperty("java.security.krb5.conf", ResourceUtils.locateFile(kerberosConfig.krb5Conf, true));
        //            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
        //            configuration.set("hadoop.security.authentication", "kerberos")
        //            UserGroupInformation.setConfiguration(configuration);
        //        }
        //        ConnectionFactory.createConnection(configuration)
        connection
    }

    override def wrap(connection: Connection) = new DefaultPooledObject[Connection](connection)

    override def validateObject(pool: PooledObject[Connection]): Boolean = !pool.getObject.isClosed

    override def destroyObject(pool: PooledObject[Connection]): Unit = {
        println("pool close")
        pool.getObject.close()
    }

    override def passivateObject(pool: PooledObject[Connection]): Unit = {}

}
