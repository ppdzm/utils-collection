package io.github.ppdzm.utils.database.handler

import io.github.ppdzm.utils.universal.implicits.ResultSetConversions.ResultSetImplicits
import org.scalatest.FunSuite

class MySQLHandlerTest extends FunSuite {

    test("scalar") {
        val handler = new MySQLHandler("jdbc:mysql://10.2.3.1:3306/sqoop_config?user=root&password=123456", null)
        //val rs = handler.query("show variables like 'binlog_format'")
        handler.query("show variables like 'binlog_format'", _.scalar[String](0, 1))
    }

}
