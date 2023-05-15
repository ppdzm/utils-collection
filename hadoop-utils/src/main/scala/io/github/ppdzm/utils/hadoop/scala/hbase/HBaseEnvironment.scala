package io.github.ppdzm.utils.hadoop.scala.hbase

import io.github.ppdzm.utils.hadoop.scala.hbase.pool.HBaseConnectionPool
import io.github.ppdzm.utils.hadoop.scala.security.KerberosConfig
import io.github.ppdzm.utils.universal.base.ResourceUtils
import org.apache.commons.pool2.ObjectPool
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}
import org.apache.hadoop.security.UserGroupInformation

trait HBaseEnvironment {
    val kerberosEnabled: Boolean
    val kerberosConfig: KerberosConfig
    val zookeeperQuorum: String
    val zookeeperPort: Int
    lazy val configuration: Configuration = {
        val conf = HBaseConfiguration.create()
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum)
        conf.setInt("hbase.zookeeper.property.clientPort", zookeeperPort)
        conf
    }
    lazy val connection: Connection = {
        if (kerberosEnabled) {
            System.setProperty("java.security.krb5.realm", kerberosConfig.realm)
            System.setProperty("java.security.krb5.kdc", kerberosConfig.kdcHost)
            System.setProperty("java.security.krb5.conf", ResourceUtils.locateFile(kerberosConfig.krb5Conf, true));
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
            configuration.set("hadoop.security.authentication", "kerberos")
            UserGroupInformation.setConfiguration(configuration);
        }
        ConnectionFactory.createConnection(configuration)
    }

    def pooledConnection: ObjectPool[Connection] = {
        HBaseConnectionPool(zookeeperQuorum, zookeeperPort, kerberosEnabled, kerberosConfig)
    }

    def close(): Unit = HBaseConnectionPool(zookeeperQuorum, zookeeperPort, kerberosEnabled, kerberosConfig).close()
}
