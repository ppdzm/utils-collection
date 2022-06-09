package io.github.ppdzm.utils.universal.config

import com.typesafe.config.ConfigFactory
import io.github.ppdzm.utils.universal.cli.Render
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.scalatest.FunSuite

import scala.collection.JavaConversions._
import scala.util.Try

/**
 * Created by yuqitao on 2017/12/6.
 */
class FileConfigTest extends FunSuite {

    test("args") {
        val config = new FileConfig("--profiles.active=test --profiles.prefix=config".split(" "))
        config.getProperties.foreach(println)
    }

    test("config 1") {
        val config: Config = new FileConfig()
        println("value of empty_config is " + config.getProperty("empty_config"))
        println("value of some_config is " + config.getProperty("some_config"))
        println("value of embedded_config is " + config.getProperty("embedded_config"))
        println("value of embedded_empty_config is " + config.getProperty("embedded_empty_config"))
        println("value of new_config is " + config.newConfigItem("new_config", "new_config_value").stringValue)
        Try {
            config.getProperty("null_config")
        }
            .getOrElse {
                println("value of null_config not found".rendering(Render.RED))
            }
        println("value of complex_config is " + config.getProperty("complex_config"))
        config.newConfigItem("un", """a:"1:2",b:"3:4"""").mapValue()
            .foreach(e => println(e.toString()))
    }

    test("implicit") {
        implicit val config: Config = new FileConfig()
        val x = config.newConfigItem("some_config", "111")
        println(x.stringValue)
        x.newValue(28237478)
        println(x.stringValue)
    }

    test("typesafe-config") {
        System.setProperty("some_config", "1234567890")
        val config1 = ConfigFactory.load("application-test.properties")
        config1.entrySet().foreach {
            entry => println(entry.getKey, entry.getValue)
        }
        println("=" * 200)
        val config2 = ConfigFactory.load("application.yaml")
        config2.entrySet().foreach {
            entry => println(entry.getKey, entry.getValue)
        }
    }

}
