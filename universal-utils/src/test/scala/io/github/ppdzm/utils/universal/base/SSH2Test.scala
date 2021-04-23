package io.github.ppdzm.utils.universal.base

import org.scalatest.FunSuite

/**
 * @author Created by Stuart Alex on 2021/4/19.
 */
class SSH2Test extends FunSuite {
    test("ssh2") {
        SSH2Utils.executeShellOnRemoteServer("10.25.21.40", null, null, "touch /home/hadoop/stuart_alex/a.txt")
    }
}
