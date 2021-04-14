package io.github.ppdzm.utils.database.pool.redis

import io.github.ppdzm.utils.universal.base.StringUtils
import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2021/2/2.
 */
class RedisPoolTest extends FunSuite {
    test("redis-pool") {
        val host = "localhost"
        val port = 6479
        val redisClientPool = RedisClientPool(host, port, "")
        while (true) {
            redisClientPool.lpush("test:items2", StringUtils.randomString(10))
            Thread.sleep(1000)
        }
    }
}
