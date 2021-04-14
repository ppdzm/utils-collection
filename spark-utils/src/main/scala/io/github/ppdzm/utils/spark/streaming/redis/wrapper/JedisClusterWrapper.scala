package io.github.ppdzm.utils.spark.streaming.redis.wrapper

import redis.clients.jedis.JedisCluster

/**
 * Created by Stuart Alex on 2017/4/5.
 */
class JedisClusterWrapper(jedisCluster: JedisCluster) extends JedisWrapper {

    override def spop(key: String) = this.jedisCluster.spop(key)

    override def lpop(key: String) = this.jedisCluster.lpop(key)

    override def close() = this.jedisCluster.close()

}