package io.github.ppdzm.utils.office.excel.scala

import io.github.ppdzm.utils.universal.implicits.BasicConversions.StringImplicits
import org.scalatest.FunSuite

class SplitTest extends FunSuite {

    test("split") {
        """plat=android,pname=com.gamezhaocha.app,"pid='945040115','e66712b7bf8'","posid=102,108,113""""
            .splitDoubleQuotedString(",")
            .foreach(println)
    }

}
