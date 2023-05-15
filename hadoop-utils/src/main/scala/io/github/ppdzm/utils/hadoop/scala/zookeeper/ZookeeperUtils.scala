package io.github.ppdzm.utils.hadoop.scala.zookeeper

import org.apache.hadoop.hdfs.server.namenode.ha.proto.HAZKInfoProtos
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.Stat

/**
 * Created by Stuart Alex on 2021/4/16.
 */
object ZookeeperUtils extends App {

    def getActiveNodeInfo(zookeeperQuorum: String, path: String): HAZKInfoProtos.ActiveNodeInfo = {
        val zooKeeper = new ZooKeeper(zookeeperQuorum, 60000, null)
        val data = zooKeeper.getData(path, true, new Stat())
        HAZKInfoProtos.ActiveNodeInfo.parseFrom(data)
    }

}
