package org.lechisoft.minifw.jdbc.datasource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DBCP2 {


    /**
     * 获取DBCP2数据源
     *
     * @param path 属性文件路径
     * @return 数据源
     */
    public static DataSource getDataSource(String path) {
        BasicDataSource dataSource = null;
        Properties props = new Properties();
        try {
            props.load(DBCP2.class.getResourceAsStream(path));
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (IOException e) {
            MiniLogger.getLogger().error("DBCP2读取属性文件" + path + "发生异常");
        } catch (Exception e) {
            MiniLogger.getLogger().error("创建DBCP2发生异常");
        }
        return dataSource;
    }

    /**
     * 获取DBCP2数据源
     *
     * @return 数据源
     */
    public static DataSource getDataSource() {
        return DBCP2.getDataSource("/jdbc.properties");
    }
}