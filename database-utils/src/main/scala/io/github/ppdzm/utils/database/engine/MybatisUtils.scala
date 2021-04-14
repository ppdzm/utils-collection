package io.github.ppdzm.utils.database.engine

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.{SqlSession, SqlSessionFactoryBuilder}

/**
 * Created by Stuart Alex on 2021/4/6.
 */
object MybatisUtils {

    def getSession(env: String): SqlSession = getSession(env, autoCommit = true)

    def getSession(env: String, autoCommit: Boolean): SqlSession = {
        val resource = "mybatis-config.xml"
        val inputStream = Resources.getResourceAsStream(resource)
        val sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, env)
        val session = sqlSessionFactory.openSession(autoCommit)
        session.getConnection.createStatement.execute("SET NAMES utf8mb4")
        session
    }

}
