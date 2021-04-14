package io.github.ppdzm.utils.database.common

import io.github.ppdzm.utils.universal.base.Enum

/**
 * Created by Stuart Alex on 2017/3/28.
 */
object DataSourceClassName extends Enum {
    type DataSourceClassName = Value
    val Apache_Derby = Value("org.apache.derby.jdbc.ClientDataSource")
    val Firebird = Value("org.firebirdsql.pool.FBSimpleDataSource")
    val H2 = Value("org.h2.jdbcx.JdbcDataSource")
    val HSQLDB = Value("org.hsqldb.jdbc.JDBCDataSource")
    val IBM_DB2 = Value("com.ibm.db2.jcc.DB2SimpleDataSource")
    val IBM_Informix = Value("com.informix.jdbcx.IfxDataSource")
    val MSSQLServer = Value("com.microsoft.sqlserver.jdbc.SQLServerDataSource")
    val MySQL = Value("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")
    val MariaDB = Value("org.mariadb.jdbc.MySQLDataSource")
    val Oracle = Value("oracle.jdbc.pool.OracleDataSource")
    val OrientDB = Value("com.orientechnologies.orient.jdbc.OrientDataSource")
    val PostgreSQL_With_pgjdbc_ng = Value("com.impossibl.postgres.jdbc.PGDataSource")
    val PostgreSQL = Value("org.postgresql.ds.PGSimpleDataSource")
    val SAP_MaxDB = Value("	com.sap.dbtech.jdbc.DriverSapDB")
    val SQLite = Value("org.sqlite.SQLiteDataSource")
    val SyBase = Value("com.sybase.jdbc4.jdbc.SybDataSource")
}
