package io.github.ppdzm.utils.spark

import io.github.ppdzm.utils.hadoop.scala.hbase.HBaseCatalog
import io.github.ppdzm.utils.universal.base.ResourceUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.spark.{HBaseContext, KeyFamilyQualifier}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.sql.Row

object HiveToHBase extends App {
    System.setProperty("spark.hive.enabled", "true")
    val sparkSession = SparkUtils.getSparkSession()
    val zookeeperQuorum = "10.2.3.2,10.2.3.3,10.2.3.4"
    val zookeeperPort = 2181
    val realm = "LIXIAOYUN.COM"
    val kdc = "10.2.3.1"
    val krb5Conf = "krb5.conf"
    lazy val configuration: Configuration = HBaseConfiguration.create()
    configuration.set("hbase.zookeeper.quorum", zookeeperQuorum)
    configuration.setInt("hbase.zookeeper.property.clientPort", zookeeperPort)
    System.setProperty("java.security.krb5.realm", realm)
    System.setProperty("java.security.krb5.kdc", kdc)
    System.setProperty("java.security.krb5.conf", ResourceUtils.locateFile(krb5Conf, true));
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
    configuration.set("hadoop.security.authentication", "kerberos")
    UserGroupInformation.setConfiguration(configuration);
    //val hbaseHandler = HBaseHandler(zookeeperQuorum, zookeeperPort, kerberosEnabled = true, KerberosConfig(realm, kdc, krb5Conf))
    val hBaseContext = new HBaseContext(sparkSession.sparkContext, configuration)
    val data = sparkSession.sql(
        """
          |SELECT reverse(cast(id as string)) as rk,
          |       cast(id as string) as id,
          |       cast(agent_id as string) as agent_id,
          |       cast(audited_at as string) as audited_at
          |FROM bms_ods.ods_bms_orders
          |""".stripMargin)
    data.persist()
    data.printSchema()
    data.show()
    val catalog = HBaseCatalog("himma_test", "bms_orders", "rk", "cf", Array("id", "agent_id", "audited_at")).toString
    println(catalog)
    val rdd = data.rdd
    val columns = data.columns
    hBaseContext.bulkLoad[Row](rdd, TableName.valueOf("himma_test:bms_orders"),
        v1 => {
            val rowKey = Bytes.toBytes(v1.getAs[String]("rk"))
            val family: Array[Byte] = Bytes.toBytes("cf")
            columns
              .map {
                  col =>
                      val qualifier = Bytes.toBytes(col)
                      val rawValue = v1.getAs[String](col)
                      if (rawValue == null) {
                          null
                      } else {
                          (new KeyFamilyQualifier(rowKey, family, qualifier), Bytes.toBytes(rawValue))
                      }
              }
              .filter(_ != null)
              .iterator
        }
        , "/user/hive-hbase/",
        maxSize = 10737418240L)
    //    data
    //      .write
    //      .mode(SaveMode.Overwrite)
    //      .options(
    //          Map(
    //              HBaseTableCatalog.tableCatalog -> catalog
    //          )
    //      )
    //      .format("org.apache.hadoop.hbase.spark")
    //      .save()
}
