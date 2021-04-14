package io.github.ppdzm.utils.hadoop.hbase

import io.github.ppdzm.utils.universal.cli.PrintConfig
import io.github.ppdzm.utils.universal.config.{Config, FileConfig}
import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Scan
import org.sa.utils.hadoop.constants.ZookeeperConfigConstants
import org.sa.utils.hadoop.hbase.implicts.HBaseImplicits._
import org.sa.utils.hadoop.hbase.pool.HBaseConnectionPool
import org.sa.utils.universal.cli.PrintConfig
import org.sa.utils.universal.config.{Config, FileConfig}
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/1/11.
 */
class HBaseTest extends FunSuite with ZookeeperConfigConstants with PrintConfig {
    override implicit protected val config: Config = FileConfig()

    test("hbase-display") {
        LoanPattern.using(HBaseConnectionPool(ZOOKEEPER_QUORUM.stringValue, ZOOKEEPER_PORT.intValue).borrowObject())(connection => {
            LoanPattern.using(connection.getTable(TableName.valueOf("MediaBasicTest")))(table => {
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

    test("HBase connection pool") {
        Array(1, 2).foreach(_ => {
            LoanPattern.using(HBaseConnectionPool(ZOOKEEPER_QUORUM.stringValue, ZOOKEEPER_PORT.intValue).borrowObject())(connection => {
                LoanPattern.using(connection.getAdmin)(admin => {
                    admin.listTableNames().map(_.getNameAsString).foreach(println)
                })
            })
        })
    }
}