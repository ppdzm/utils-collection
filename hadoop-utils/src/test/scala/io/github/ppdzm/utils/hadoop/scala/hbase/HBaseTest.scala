package io.github.ppdzm.utils.hadoop.scala.hbase

import io.github.ppdzm.utils.hadoop.scala.hbase.implicts.HBaseImplicits._
import io.github.ppdzm.utils.hadoop.scala.constants.ZookeeperConfigConstants
import io.github.ppdzm.utils.hadoop.scala.hbase.pool.HBaseConnectionPool
import io.github.ppdzm.utils.hadoop.scala.security.KerberosConfig
import io.github.ppdzm.utils.universal.base.ResourceUtils
import io.github.ppdzm.utils.universal.cli.PrintConfig
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Scan}
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.security.UserGroupInformation
import org.scalatest.FunSuite
import scalikejdbc.LoanPattern

/**
 * Created by Stuart Alex on 2017/1/11.
 */
class HBaseTest extends FunSuite with ZookeeperConfigConstants with PrintConfig {
    override implicit protected val config: Config = new FileConfig()

    test("connect-kerberos-hbase") {
        val krb5Conf: String = "/Users/stuart_alex/Documents/projects/lxy-data-warehouse/extended/kerberos/krb5.conf"
        val configuration = HBaseConfiguration.create()
        configuration.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM.stringValue)
        configuration.setInt("hbase.zookeeper.property.clientPort", ZOOKEEPER_PORT.intValue)
        System.setProperty("java.security.krb5.realm", "LIXIAOYUN.COM")
        System.setProperty("java.security.krb5.kdc", "cdh1")
        System.setProperty("java.security.krb5.conf", krb5Conf);
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
        configuration.set("hadoop.security.authentication", "kerberos")
        UserGroupInformation.setConfiguration(configuration);
        val connection = ConnectionFactory.createConnection(configuration)
        val admin = connection.getAdmin
        val tableNames = admin.listNamespaceDescriptors()
        tableNames.foreach {
            ns =>
                println(ns.getName)
        }
    }

    test("HBase connection pool") {
        Array(1, 2).foreach(_ => {
            LoanPattern.using(HBaseConnectionPool(ZOOKEEPER_QUORUM.stringValue, ZOOKEEPER_PORT.intValue).borrowObject())(connection => {
                LoanPattern.using(connection.getAdmin)(admin => {
                    admin.listTableNames().map(_.getNameAsString).foreach(println)
                })
            })
        })
    }

    test("HBase connection pool2") {
        val handler = HBaseHandler(
            ZOOKEEPER_QUORUM.stringValue(),
            ZOOKEEPER_PORT.intValue(),
            kerberosEnabled = true,
            KerberosConfig(
                "LIXIAOYUN.COM",
                "cdh1",
                ResourceUtils.locateFile("krb5.conf", true)
            )
        )
        handler.createNamespace("himma")
        println("crete namespace succeeded")
//        handler.disableTable("himma:himma_test")
//        println("disable table succeeded")
//        handler.dropTable("himma:himma_test")
//        println("drop table succeeded")
        handler.createTable("himma:himma_test", Array("cf"), startKey = "00", endKey = "99")
        println("crete table succeeded")
        handler.put("himma:himma_test", "0001", "cf", Map("a" -> "1", "b" -> "2"))
        println(System.currentTimeMillis() + " put 0001 succeeded")
        handler.put("himma:himma_test", "0002", "cf", Map("a" -> "11", "b" -> "22"))
        println(System.currentTimeMillis() + " put 0002 succeeded")
        handler.put("himma:himma_test", "0003", "cf", Map("a" -> "111", "b" -> "222"))
        println(System.currentTimeMillis() + " put 0003 succeeded")
        handler.put("himma:himma_test", "0004", "cf", Map("a" -> "1111", "b" -> "2222"))
        println(System.currentTimeMillis() + " put 0004 succeeded")
        handler.put("himma:himma_test", "0005", "cf", Map("a" -> "1111", "b" -> "2222"))
        println(System.currentTimeMillis() + " put 0005 succeeded")
        handler.put("himma:himma_test", "0006", "cf", Map("a" -> "1111", "b" -> "2222"))
        println(System.currentTimeMillis() + " put 0006 succeeded")
        handler.put("himma:himma_test", "0007", "cf", Map("a" -> "1111", "b" -> "2222"))
        println(System.currentTimeMillis() + " put 0007 succeeded")
    }

    test("hbase-display") {
        LoanPattern.using(HBaseConnectionPool(ZOOKEEPER_QUORUM.stringValue, ZOOKEEPER_PORT.intValue).borrowObject())(connection => {
            LoanPattern.using(connection.getTable(TableName.valueOf("himma:himma_test")))(table => {
                val scan = new Scan()
                val iterator = table.getScanner(scan).iterator()
                var break = false
                while (iterator.hasNext && !break) {
                    iterator.next().prettyShow(render, alignment, linefeed)
                    break = true
                }
            })
        })
    }

    test("kerberos-hbase-display") {
        LoanPattern.using(
            HBaseConnectionPool(
                ZOOKEEPER_QUORUM.stringValue,
                ZOOKEEPER_PORT.intValue,
                kerberosEnabled = true,
                KerberosConfig(
                    "LIXIAOYUN.COM",
                    "cdh1",
                    ResourceUtils.locateFile("krb5.conf", true)
                )
            ).borrowObject()
        )(connection => {
            LoanPattern.using(connection.getTable(TableName.valueOf("himma:himma_test")))(table => {
                val scan = new Scan()
                val iterator = table.getScanner(scan).iterator()
                var break = false
                while (iterator.hasNext && !break) {
                    iterator.next().prettyShow(render, alignment, linefeed)
                    //break = true
                }
            })
        })
    }

    test("hbase bulk delete") {
        val handler = HBaseHandler(
            ZOOKEEPER_QUORUM.stringValue(),
            ZOOKEEPER_PORT.intValue(),
            kerberosEnabled = true,
            KerberosConfig(
                "LIXIAOYUN.COM",
                "cdh1",
                ResourceUtils.locateFile("krb5.conf", true)
            )
        )
        handler.bulkDelete("himma:sync_logs", ".+sync_logs", 1000)
    }

    test("create table") {
        val handler = HBaseHandler(
            ZOOKEEPER_QUORUM.stringValue(),
            ZOOKEEPER_PORT.intValue(),
            kerberosEnabled = true,
            KerberosConfig(
                "LIXIAOYUN.COM",
                "cdh1",
                ResourceUtils.locateFile("krb5.conf", true)
            )
        )
        //handler.createTable("abc", Array("cf1", "cf2"))
        handler.put("abc", "0001", "cf1", Map("a1" -> "aaa", "b1" -> "bbb"))
        handler.put("abc", "0002", "cf2", Map("a1" -> "aaa", "b1" -> "bbb"))
    }


}