package io.github.ppdzm.utils.universal.base

import org.apache.commons.io.FilenameUtils
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2021/4/8.
 */
class CommonTest extends FunSuite with Logging {
    test("filename-utils") {
        println(FilenameUtils.removeExtension("a.txt"))
        println(FilenameUtils.getExtension("a.txt"))
    }
}
