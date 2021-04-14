package io.github.ppdzm.utils.database.common

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object Drivers {
    val MySQL: Driver = Driver("com.mysql.jdbc.Driver")
    val Hive: Driver = Driver("org.apache.hive.jdbc.HiveDriver")
    val SQLServer: Driver = Driver("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    val JTDS: Driver = Driver("net.sourceforge.jtds.jdbc.Driver")
    val PRESTO: Driver = Driver("com.facebook.presto.jdbc.PrestoDriver")

    case class Driver(name: String) {

        def load(): Unit = {
            Class.forName(name)
        }

        override def toString: String = name

    }

}
