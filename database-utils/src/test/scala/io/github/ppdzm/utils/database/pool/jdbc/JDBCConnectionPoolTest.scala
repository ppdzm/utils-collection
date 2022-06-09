package io.github.ppdzm.utils.database.pool.jdbc

import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/3/29.
 */
class JDBCConnectionPoolTest extends FunSuite {

    test("hikariCP") {
        val url = "jdbc:mysql://127.0.0.1/?user=root&password=123456&useSSL=false"
        Array(1, 2).foreach(_ => {
            LoanPattern.using(HikariConnectionPool(jdbcUrl = url).borrow())(connection => {
                LoanPattern.using(connection.prepareStatement("select table_name from information_schema.tables limit 1"))(ps => {
                    LoanPattern.using(ps.executeQuery())(rs => {
                        while (rs.next())
                            println(rs.getString(1))
                    })
                })
            })
        })
    }

}