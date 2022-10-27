package io.github.ppdzm.utils.database.handler

import io.github.ppdzm.utils.universal.base.Logging

import java.sql.{PreparedStatement, ResultSet}

/**
 * Created by Stuart Alex on 2021/4/6.
 */
trait RDBHandler {
    protected lazy val logging = new Logging(getClass)
    protected val url: String

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     */
    def createDatabase(database: String): Unit

    /**
     * 创建表
     *
     * @param createSql 建表语句
     */
    def createTable(createSql: String): Unit

    /**
     * 创建表
     *
     * @param database         数据库名称
     * @param table            表名称
     * @param columnDefinition 表字段定义
     */
    def createTable(database: String, table: String, columnDefinition: Map[String, String]): Unit

    /**
     * 删除数据库
     *
     * @param database 数据库名称
     * @param cascade  若数据库非空，则需指定cascade，否则将抛出异常
     */
    def dropDatabase(database: String, cascade: Boolean): Unit

    /**
     * 删除表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def dropTable(database: String, table: String): Unit

    /**
     * 执行sql语句
     *
     * @param statement sql语句
     */
    def execute(statement: String): Unit

    /**
     * 判断某个数据库是否存在
     *
     * @param database 数据库名称
     * @return
     */
    def exists(database: String): Boolean

    /**
     * 判断某张表是否存在
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def exists(database: String, table: String): Boolean

    /**
     * 列出所有数据库
     *
     * @return
     */
    def listDatabases(regexp: String = null): List[String]

    /**
     * 列出所有表
     *
     * @param database 数据库名称
     * @return
     */
    def listTables(database: String, regexp: String = null): List[String]

    /**
     * 查询
     *
     * @param sql sql语句
     * @return
     */
    def query[T](sql: String, f: ResultSet => T): T

    /**
     * 查询
     *
     * @param sql        sql语句
     * @param parameters sql语句参数
     * @return
     */
    def query[T](sql: String, parameters: Array[Any], f: ResultSet => T): T

    /**
     * 查询hive外部表创建语句
     *
     * @param database 数据库名称
     * @param table    表名称
     * @return
     */
    def showCreateTable(database: String, table: String): String

    /**
     * 清空表
     *
     * @param database 数据库名称
     * @param table    表名称
     */
    def truncate(database: String, table: String): Unit

    /**
     * PreparedStatement的隐式实现
     *
     * @param ps PreparedStatement
     */
    implicit class PreparedStatementImplicits(ps: PreparedStatement) {

        /**
         * 设置PreparedStatement的参数
         *
         * @param parameters 参数值数组
         * @return
         */
        def setParameters(parameters: Array[Any]): PreparedStatement = {
            if (parameters == null)
                return ps
            parameters
              .zipWithIndex
              .foreach {
                  case (v, i) => ps.setParameter(i, v)
              }
            ps
        }

        /**
         * 设置PreparedStatement的参数
         *
         * @param parameterIndex 参数位置
         * @param value          参数值
         * @return
         */
        def setParameter(parameterIndex: Int, value: Any): PreparedStatement = {
            ps.setParameter(parameterIndex, value, value.getClass.getSimpleName.toLowerCase)
        }

        /**
         * 设置PreparedStatement的参数
         *
         * @param columnNameValuePair 参数，也即字段名称——字段值对
         * @param columnNameTypePair  字段名称——类型对
         * @param startIndex          起始索引
         * @return
         */
        def setParameters(columnNameValuePair: Map[String, Any], columnNameTypePair: Map[String, String], startIndex: Int): PreparedStatement = {
            if (columnNameValuePair == null)
                return ps
            columnNameValuePair
              .toList
              .sortBy(_._1)
              .zipWithIndex
              .foreach {
                  case ((k, v), i) =>
                      val columnType = columnNameTypePair(k)
                      ps.setParameter(startIndex + i, v, columnType)
              }
            ps
        }

        /**
         * 设置PreparedStatement的参数
         *
         * @param parameterIndex 参数位置
         * @param value          参数值
         * @param columnType     字段类型
         * @return
         */
        def setParameter(parameterIndex: Int, value: Any, columnType: String): PreparedStatement = {
            columnType match {
                case "varchar" | "string" => ps.setString(parameterIndex, value.asInstanceOf[String])
                case "bigint" | "long" => ps.setLong(parameterIndex, value.asInstanceOf[Long])
                case "int" | "integer" => ps.setLong(parameterIndex, value.asInstanceOf[Int])
                case "tinyint" =>
                    val intValue =
                        value match {
                            case b: Boolean => if (b) 1 else 0
                            case _: Int => value
                        }
                    ps.setInt(parameterIndex, intValue.asInstanceOf[Int])
                case "boolean" => ps.setBoolean(parameterIndex, value.asInstanceOf[Boolean])
                case "float" => ps.setFloat(parameterIndex, value.asInstanceOf[Float])
                case "double" => ps.setDouble(parameterIndex, value.asInstanceOf[Double])
                case _ => ps.setObject(parameterIndex, value)
            }
            ps
        }

    }

}
