package io.github.ppdzm.utils.database.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author stuartalex
 */
public class HikariDataSourceFactory implements DataSourceFactory {
    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(this.properties.getProperty("driver"));
        dataSource.setJdbcUrl(this.properties.getProperty("url"));
        dataSource.setUsername(this.properties.getProperty("username"));
        dataSource.setPassword(this.properties.getProperty("password"));
        dataSource.setConnectionTestQuery("select 1");
        dataSource.setConnectionInitSql("SET NAMES utf8mb4");
        dataSource.setIdleTimeout(60000);
        dataSource.setConnectionTimeout(60000);
        dataSource.setValidationTimeout(3000);
        try {
            dataSource.setLoginTimeout(5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.setMaxLifetime(60000);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }
}
