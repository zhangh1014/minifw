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
        MiniLogger.debug("正在创建DBCP2数据源...");
        BasicDataSource dataSource = null;

        MiniLogger.debug("正在读取属性文件：" + path);
        Properties props = new Properties();
        try {
            props.load(DBCP2.class.getResourceAsStream(path));
            MiniLogger.debug("读取属性文件成功");

            dataSource = BasicDataSourceFactory.createDataSource(props);
            MiniLogger.debug("创建DBCP2数据源成功");
            return dataSource;
        } catch (IOException e) {
            props = null;
            MiniLogger.error("读取属性文件发生异常");
            return null;
        } catch (Exception e) {
            props = null;
            MiniLogger.error("创建DBCP2数据源发生异常");
            return null;
        }
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