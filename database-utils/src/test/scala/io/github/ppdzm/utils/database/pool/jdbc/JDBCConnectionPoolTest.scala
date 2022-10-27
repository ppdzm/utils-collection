package io.github.ppdzm.utils.database.pool.jdbc

import io.github.ppdzm.utils.database.pool.mysql.MySQLHandlerPool
import io.github.ppdzm.utils.universal.feature.Pool
import io.github.ppdzm.utils.universal.implicits.ResultSetConversions.ResultSetImplicits
import io.github.ppdzm.utils.universal.implicits.SeqConversions.SeqComparableImplicits
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/3/29.
 */
class JDBCConnectionPoolTest extends FunSuite {

    test("hikariCP") {
        val url = "jdbc:mysql://10.2.3.1:3306/information_schema?user=root&password=123456&characterEncoding=utf8&transformedBitIsBoolean=true&rewriteBatchedStatements=true&useSSL=false&tinyInt1isBit=false&failOverReadOnly=false&useunicode=true&driver=com.mysql.jdbc.Driver&autoReconnect=true&zeroDateTimeBehavior=convertToNull&yearIsDateType=false"
        var index = 1
        Pool.borrow(MySQLHandlerPool(url, null)) {
            handler =>
                val tables = handler.listTables("hive")
                tables
                  .foreach {
                      table =>
                          val sql = s"select column_name from information_schema.columns where table_schema='hive' and table_name='$table' and column_key='PRI'"
                          println(index + " " + table + "=>" + handler.query(sql, _.singleColumnList(0, null2Empty = true).ascending.mkString(" ")))
                          index += 1
                  }
        }
        //        Array(1, 2).foreach(_ => {
        //            LoanPattern.using(HikariConnectionPool(jdbcUrl = url).borrow())(connection => {
        //                LoanPattern.using(connection.prepareStatement("select table_name from information_schema.tables limit 1"))(ps => {
        //                    LoanPattern.using(ps.executeQuery())(rs => {
        //                        while (rs.next())
        //                            println(rs.getString(1))
        //                    })
        //                })
        //            })
        //        })
    }

}