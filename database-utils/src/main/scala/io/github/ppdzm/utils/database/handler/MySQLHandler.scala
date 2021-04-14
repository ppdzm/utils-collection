package io.github.ppdzm.utils.database.handler

import java.sql.ResultSet

import io.github.ppdzm.utils.database.connection.MySQLConnection
import io.github.ppdzm.utils.universal.base.{Logging, StringUtils}
import io.github.ppdzm.utils.universal.feature.LoanPattern
import org.sa.utils.universal.base.{Logging, StringUtils}
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import io.github.ppdzm.utils.universal.implicits.ResultSetConversions._

/**
 * Created by jingsz on 4/28/18.
 */
case class MySQLHandler(url: String, extraProperties: Map[String, AnyRef]) extends RDBHandler with Logging {

    /**
     * 查询binlog_format
     *
     * @return
     */
    def binlogFormat(): String = {
        query("show variables like 'binlog_format'").scalar(0, 1)
    }

    /**
     * 查询
     *
     * @param statement sql语句
     * @return
     */
    def query(statement: String): ResultSet = {
        MySQLConnection.getStatement(url, extraProperties).executeQuery(statement)
    }

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     */
    def createDatabase(database: String): Unit = {
        execute(s"create database if not exists $database")
    }

    /**
     * 执行sql语句
     *
     * @param statement sql语句
     */
    def execute(statement: String): Unit = {
        LoanPattern.using(MySQLConnection.getStatement(url, extraProperties))(ps => {
            ps.execute(statement)
        })
    }

    /**
     * 创建表
     *
     * @param createSql 建表语句
     */
    def createTable(createSql: String): Unit = {
        execute(createSql)
    }

    /**
     * 创建表
     *
     * @param database         数据库名称
     * @param table            表名称
     * @param columnDefinition 表字段定义
     */
    def createTable(database: String, table: String, columnDefinition: Map[String, String]): Unit = {
        execute(s"create table $database.$table(${columnDefinition.map(e => e._1 + " " + e._2).mkString(",")})")
    }

    /**
     * 删除数据库
     *
     * @param database 数据库名称
     * @param cascade  若数据库非空，则需指定cascade，否则将抛出异常
     */
    def dropDatabase(database: String, cascade: Boolean): Unit = {
        execute(s"drop database if exists $database")
    }

    /**
     * 删除表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def dropTable(database: String, table: String): Unit = {
        execute(s"drop table if exists ${url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'))}.$table")
    }

    /**
     * 执行sql
     *
     * @param statement  sql语句
     * @param parameters sql语句参数
     * @return Boolean（是否成功）
     */
    def execute(statement: String, parameters: Array[String]): Boolean = {
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, properties = extraProperties, sql = statement))(ps => {
            parameters.indices.filter(parameters(_) != null).foreach(i => ps.setObject(i + 1, parameters(i)))
            ps.execute()
        })
    }

    /**
     * 判断某个数据库是否存在
     *
     * @param database 数据库名称
     * @return
     */
    def exists(database: String): Boolean = {
        listDatabases().contains(database)
    }

    /**
     * 列出所有数据库
     *
     * @return
     */
    def listDatabases(regexp: String = null): List[String] = {
        if (regexp.notNullAndEmpty)
            query("show databases").singleColumnList(0).filter(_.matches(regexp))
        else
            query("show databases").singleColumnList(0)
    }

    /**
     * 判断某张表是否存在
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def exists(database: String, table: String): Boolean = {
        listTables(database).contains(table)
    }

    /**
     * 列出所有表
     *
     * @param database 数据库名称
     * @return
     */
    def listTables(database: String, regexp: String = null): List[String] = {
        val database = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"))
        val sql = s"select table_name from information_schema.tables where table_schema='$database'"
        if (regexp.notNullAndEmpty)
            query(sql).singleColumnList(0).filter(_.matches(regexp))
        else
            query(sql).singleColumnList(0)
    }

    /**
     * 查询global binlog_format
     *
     * @return
     */
    def globalBinlogFormat(): String = {
        query("show global variables like 'binlog_format'").scalar(0, 1)
    }

    /**
     * 查询
     *
     * @param statement  sql语句
     * @param parameters sql语句参数
     * @return
     */
    def query(statement: String, parameters: Array[String]): ResultSet = {
        LoanPattern.using(MySQLConnection.getPreparedStatement(url, properties = extraProperties, sql = statement)) {
            ps =>
                parameters.indices.filterNot(parameters(_).isNull).foreach(i => ps.setObject(i + 1, parameters(i)))
                ps.executeQuery()
        }
    }

    /**
     * 随机向某张表内插入一些数据
     *
     * @param table   目标表名称
     * @param columns 目标表字段列表
     * @param count   插入随机数字的行数
     */
    def randomInsert(table: String, columns: Array[String], count: Int): Unit = {
        LoanPattern.using(MySQLConnection.getConnection(url, extraProperties))(connection => {
            connection.setAutoCommit(false)
            val statement = s"insert into $table(${columns.mkString(",")}) values (${List.fill(columns.length)("?").mkString(",")})"
            val ps = connection.prepareStatement(statement)
            (1 to count).foreach(_ => {
                val rsa = Array.fill(columns.length)(StringUtils.randomString(20))
                rsa.indices.foreach(j => ps.setObject(j + 1, rsa(j)))
                ps.addBatch()
            })
            ps.executeBatch()
            connection.commit()
        })
    }

    /**
     * 查询hive外部表创建语句
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def showCreateTable(database: String, table: String): String = {
        query(s"show create table $database.$table").singleColumnList(0).mkString(" ")
    }

    /**
     * 清空表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def truncate(database: String, table: String): Unit = {
        execute(s"truncate table $database.$table")
    }

    //  def update(url: String,
    //             table: String,
    //             pkName: String,
    //             columnsMapList: List[Map[String, Any]],
    //             columnsType: Map[String, String]): Unit = {
    //    columnsMapList.map {
    //      columnsMap =>
    //        val validColumnsMap = columnsMap.filter(_._2 != null)
    //        val updateStr =
    //          fieldsMapToUpdateStr(validColumnsMap.keys.toList.sorted)
    //        (updateStr, validColumnsMap)
    //    }
    //      .groupBy(_._1)
    //      .mapValues(_.map(_._2))
    //      .foreach {
    //        case (updateStr, subColumnsMapList) =>
    //          val sql = s"update $table set $updateStr where $pkName=?"
    //          val ps = MySQLConnection.getPreparedStatement(url, sql)
    //
    //          subColumnsMapList
    //            .filter { columnsMap =>
    //              columnsMap.contains(pkName)
    //            }
    //            .foreach { columnsMap =>
    //              var i = 1
    //              columnsMap.toSeq.sortBy(_._1).foreach {
    //                case (k, v) =>
    //                  val columnType = columnsType(k)
    //                  preparedStatementSetValue(ps, i, v, columnType)
    //                  i += 1
    //              }
    //
    //              val pkValue = columnsMap(pkName)
    //              val pkType = columnsType(pkName)
    //              preparedStatementSetValue(ps, i, pkValue, pkType)
    //              ps.addBatch()
    //            }
    //          ps.executeBatch()
    //          ps.clearBatch()
    //      }
    //  }
    //
    //  def insertOrUpdateBatch(url: String,
    //                          table: String,
    //                          columnsMaps: List[Map[String, Any]],
    //                          columnsType: Map[String, String]): List[Int] = {
    //    if (columnsMaps.isEmpty) {
    //      return List()
    //    }
    //
    //    val columnsMapSample = columnsMaps.head
    //    val keysString = columnsMapSample.keySet.toList.sorted.mkString(",")
    //    val valuesString = columnsMapSample.toSeq.sortBy(_._1).map(_ => "?").mkString(",")
    //    val updateStr = fieldsMapToUpdateStr(columnsMapSample.keys.toList.sorted)
    //    val sql =
    //      s"""
    //         |insert into $table ($keysString) values ($valuesString)
    //         |on duplicate key update $updateStr
    //       """.stripMargin
    //    val ps = MySQLConnection.getPreparedStatement(url, sql)
    //
    //    columnsMaps.foreach {
    //      columnsMap =>
    //        var i = 1
    //        (1 to 2).foreach {
    //          _ =>
    //            columnsMap.toSeq.sortBy(_._1).foreach {
    //              case (k, v) =>
    //                val columnType = columnsType(k)
    //                preparedStatementSetValue(ps, i, v, columnType)
    //                i += 1
    //            }
    //        }
    //        ps.addBatch()
    //    }
    //    ps.executeBatch().toList
    //  }
    //
    //  def delete(url: String,
    //             table: String,
    //             pkName: String,
    //             pkValue: Any,
    //             pkType: String): Unit = {
    //    val sql = s"delete from $table where $pkName=?"
    //    val ps = MySQLConnection.getPreparedStatement(url, sql)
    //    preparedStatementSetValue(ps, 1, pkValue, pkType)
    //    ps.executeUpdate
    //  }
    //
    //  def checkPkType(pkName: String, pkValue: Any, pkType: String): Boolean = {
    //    val valid = checkType(pkValue, pkType)
    //    if (!valid) {
    //      this.logInfo(s"pk $pkName $pkValue not match $pkType", out = true)
    //    }
    //    valid
    //  }
    //
    //  def checkType(value: Any, columnType: String): Boolean = {
    //    columnType match {
    //      case "varchar" => value.isInstanceOf[String]
    //      case "bigint" => value.isInstanceOf[Long]
    //      case "int" => value.isInstanceOf[Int]
    //      case "tinyint" => value.isInstanceOf[Int]
    //      case "boolean" => value.isInstanceOf[Boolean]
    //      case "float" => value.isInstanceOf[Float]
    //      case "double" => value.isInstanceOf[Double]
    //      case otherType => throw new Exception(s"$otherType is not processed")
    //    }
    //  }
    //
    //  /**
    //   * for java
    //   */
    //
    //  def update(url: String,
    //             table: String,
    //             pkName: String,
    //             columnsMap: java.util.Map[String, Any],
    //             columnsType: java.util.Map[String, String]): Int = {
    //    update(url, table, pkName, columnsMap.toMap, columnsType.toMap)
    //  }
    //
    //  def update(url: String,
    //             table: String,
    //             pkName: String,
    //             columnsMap: Map[String, Any],
    //             columnsType: Map[String, String]): Int = {
    //    val validColumnsMap = columnsMap.filter(_._2 != null)
    //    if (!validColumnsMap.contains(pkName)) {
    //      return 0
    //    }
    //    val updateStr = fieldsMapToUpdateStr(validColumnsMap.keys.toList.sorted)
    //    val sql = s"update $table set $updateStr where $pkName=?"
    //
    //    val ps = MySQLConnection.getPreparedStatement(url, sql)
    //    var i = 1
    //
    //    validColumnsMap.toSeq.sortBy(_._1).foreach {
    //      case (k, v) =>
    //        val columnType = columnsType(k)
    //        preparedStatementSetValue(ps, i, v, columnType)
    //        i += 1
    //    }
    //    val pkValue = validColumnsMap(pkName)
    //    val pkType = columnsType(pkName)
    //    preparedStatementSetValue(ps, i, pkValue, pkType)
    //    ps.executeUpdate
    //  }
    //
    //  private def fieldsMapToUpdateStr(fields: Iterable[String]) = {
    //    fields
    //      .map { key =>
    //        s"$key=?"
    //      }
    //      .mkString(", ")
    //  }
    //
    //  private def preparedStatementSetValue(ps: PreparedStatement,
    //                                        parameterIndex: Int,
    //                                        value: Any,
    //                                        columnType: String) = {
    //    columnType match {
    //      case "varchar" =>
    //        ps.setString(parameterIndex, value.asInstanceOf[String])
    //      case "bigint" => ps.setLong(parameterIndex, value.asInstanceOf[Long])
    //      case "int" => ps.setLong(parameterIndex, value.asInstanceOf[Int])
    //      case "tinyint" =>
    //        val intValue =
    //          value match {
    //            case b: Boolean => if (b) 1 else 0
    //            case _: Int => value
    //          }
    //        ps.setInt(parameterIndex, intValue.asInstanceOf[Int])
    //      case "boolean" =>
    //        ps.setBoolean(parameterIndex, value.asInstanceOf[Boolean])
    //      case "float" => ps.setFloat(parameterIndex, value.asInstanceOf[Float])
    //      case "double" => ps.setDouble(parameterIndex, value.asInstanceOf[Double])
    //      case otherType => throw new Exception(s"$otherType is not processed")
    //    }
    //  }
    //
    //  def insertOrUpdate(url: String,
    //                     table: String,
    //                     pkName: String,
    //                     columnsMap: java.util.Map[String, Any],
    //                     columnsType: java.util.Map[String, String]): Int = {
    //    insertOrUpdate(url, table, pkName, columnsMap.toMap, columnsType.toMap)
    //  }
    //
    //  def insertOrUpdate(url: String,
    //                     table: String,
    //                     pkName: String,
    //                     columnsMap: Map[String, Any],
    //                     columnsType: Map[String, String]): Int = {
    //    val validColumnsMap = columnsMap.filter(_._2 != null)
    //    if (!validColumnsMap.contains(pkName)) {
    //      return 0
    //    }
    //    val keysString = validColumnsMap.keySet.toList.sorted.mkString(",")
    //    val valuesString = validColumnsMap.toSeq.sortBy(_._1).map(_ => "?").mkString(",")
    //    val updateStr = fieldsMapToUpdateStr(validColumnsMap.keys.toList.sorted)
    //
    //    val sql =
    //      s"""
    //         |insert into $table ($keysString) values ($valuesString)
    //         |on duplicate key update $updateStr
    //       """.stripMargin
    //    val ps = MySQLConnection.getPreparedStatement(url, sql)
    //    var i = 1
    //    (1 to 2).foreach {
    //      _ =>
    //        validColumnsMap.toSeq.sortBy(_._1).foreach {
    //          case (k, v) =>
    //            val columnType = columnsType(k)
    //            preparedStatementSetValue(ps, i, v, columnType)
    //            i += 1
    //        }
    //    }
    //    ps.executeUpdate
    //  }
    //
    //  def query(mysqlUrl: String,
    //            sql: String,
    //            columns: java.util.Collection[String]): java.util.Iterator[java.util.Map[String, Any]] = {
    //    val res = query(mysqlUrl, sql, columns.toList)
    //    asJavaIterator(res.map(mapAsJavaMap))
    //  }
    //
    //  def query(mysqlUrl: String,
    //            sql: String,
    //            columns: Iterable[String]): Iterator[Map[String, Any]] = {
    //    val conn = MySQLConnection.getConnection(mysqlUrl)
    //    val stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    //    stmt.setFetchSize(Integer.MIN_VALUE)
    //    val resultSet = stmt.executeQuery(sql)
    //    val columnsType = getColumnsType(resultSet.getMetaData)
    //
    //    new Iterator[Map[String, Any]] {
    //      def hasNext: Boolean = resultSet.next()
    //
    //      def next(): Map[String, Any] = {
    //        columns.map {
    //          column =>
    //            val columnType = columnsType(column)
    //            val value =
    //              columnType match {
    //                case "varchar" => resultSet.getString(column)
    //                case "bigint" => resultSet.getLong(column)
    //                case "int" => resultSet.getLong(column)
    //                case "tinyint" => resultSet.getInt(column)
    //                case "boolean" => resultSet.getBoolean(column)
    //                case "float" => resultSet.getFloat(column)
    //                case "double" => resultSet.getDouble(column)
    //                case otherType =>
    //                  throw new Exception(s"$otherType is not processed")
    //              }
    //            (column, value)
    //        }.toMap
    //      }
    //    }
    //  }
    //
    //  def getColumnsType(resMetaData: ResultSetMetaData): Map[String, String] = {
    //    val columnCount = resMetaData.getColumnCount
    //    (1 to columnCount).map {
    //      columnIndex =>
    //        val columnName = resMetaData.getColumnName(columnIndex)
    //        val columnType = resMetaData.getColumnTypeName(columnIndex).split(" ").head.toLowerCase
    //        (columnName, columnType)
    //    }.toMap
    //  }
    //
    //  def getColumnsTypeForJava(mysqlUrl: String, tableName: String): java.util.Map[String, String] = {
    //    mapAsJavaMap(getColumnsType(mysqlUrl, tableName))
    //  }
    //
    //  def getColumnsType(mysqlUrl: String, tableName: String): Map[String, String] = {
    //    val sql = s"select * from $tableName where 1<0"
    //    val conn = MySQLConnection.getConnection(mysqlUrl)
    //    val stmt = conn.createStatement()
    //    val res = stmt.executeQuery(sql)
    //    val resMetaData = res.getMetaData
    //    val columnCount = resMetaData.getColumnCount
    //    (1 to columnCount).map {
    //      columnIndex =>
    //        val columnName = resMetaData.getColumnName(columnIndex)
    //        val columnType = resMetaData.getColumnTypeName(columnIndex).split(" ").head.toLowerCase
    //        (columnName, columnType)
    //    }.toMap
    //  }
    //
    //  def convertMapValueToString(m: java.util.Map[String, Any]): java.util.Map[String, String] = {
    //    val nm = new java.util.HashMap[String, String]()
    //    m.foreach {
    //      case (k, v) => nm.put(k, v.toString)
    //    }
    //    nm
    //  }

}
