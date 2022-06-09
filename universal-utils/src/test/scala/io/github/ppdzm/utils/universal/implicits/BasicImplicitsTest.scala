package io.github.ppdzm.utils.universal.implicits

import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.scalatest.FunSuite

/**
 * @author StuartAlex on 2019-07-30 16:19
 */
class BasicImplicitsTest extends FunSuite {

    test("trim comment") {
        println("select * from table where some_column like '%--%';    -- this is a real comment".trimComment)
        println("'    -- this is not a comment'".trimComment)
    }

    test("split") {
        """plat=android,pname=com.xyz.app,"pid=945040115','e66712b7bf8","posid=102,108,113""""
            .splitDoubleQuotedString(",")
            .foreach(println)
    }

}
