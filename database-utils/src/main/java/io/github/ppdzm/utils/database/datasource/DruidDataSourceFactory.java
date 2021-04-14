package io.github.ppdzm.utils.database.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author yangshuai on 2018-12-26.
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties props;

    @Override
    public DataSource getDataSource() {
        System.out.println(this.props);
        DruidDataSource dds = new DruidDataSource();
        dds.setUrl(this.props.getProperty("url"));
        dds.setUsername(this.props.getProperty("username"));
        dds.setPassword(this.props.getProperty("password"));
        dds.setValidationQuery("select 'x'");
        dds.setTestWhileIdle(true);
        dds.setTestOnBorrow(true);
        dds.setTestOnReturn(false);
        dds.setTimeBetweenEvictionRunsMillis(Long.parseLong(this.props.getProperty("timeBetweenEvictionRunsMillis")));
        dds.setMinEvictableIdleTimeMillis(Long.parseLong(this.props.getProperty("minEvictableIdleTimeMillis")));
        // 其他配置可以根据MyBatis主配置文件进行配置
        try {
            dds.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dds;
    }

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

}
