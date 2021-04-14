package io.github.ppdzm.utils.spark.streaming.redis.wrapper

import redis.clients.jedis.Jedis

/**
 * Created by Stuart Alex on 2017/4/5.
 */
class JedisSingletonWrapper(jedis: Jedis) extends JedisWrapper {

    override def spop(key: String) = this.jedis.spop(key)

    override def lpop(key: String) = this.jedis.lpop(key)

    override def close() = this.jedis.close()

}