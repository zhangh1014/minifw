package org.lechisoft.minifw.jdbc.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

public class C3P0 {

    /**
     * 获取C3P0数据源
     *
     * @param path 属性文件路径
     * @return 数据源
     */
    public static DataSource getDataSource(String path) {
        MiniLogger.debug("正在创建C3P0数据源...");
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        MiniLogger.debug("正在读取属性文件：" + path);
        Properties props = new Properties();
        try {
            props.load(C3P0.class.getResourceAsStream(path));
            MiniLogger.debug("读取属性文件成功");
        } catch (IOException e) {
            props = null;
            MiniLogger.error("读取属性文件发生异常");
            return null;
        }

        // 动态为数据源属性赋值
        MiniLogger.debug("正在配置数据源...");
        Set<Object> keySet = props.keySet();
        for (Object object : keySet) {
            String key = object.toString();
            String value = props.getProperty(key);

            String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method m;
            try {
                m = ComboPooledDataSource.class.getMethod(methodName, String.class);
            } catch (NoSuchMethodException e1) {
                try {
                    m = ComboPooledDataSource.class.getMethod(methodName, Integer.class);
                } catch (NoSuchMethodException e2) {
                    m = null;
                }
            }

            if (null == m) {
                MiniLogger.warn("属性" + key + "不存在");
            } else {
                try {
                    m.invoke(dataSource, value);
                    MiniLogger.debug(key + "=" + value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    MiniLogger.warn("属性" + key + "发生异常");
                }
            }
        }
        MiniLogger.debug("配置数据源成功");

        MiniLogger.debug("创建C3P0数据源成功");
        return dataSource;
    }

    /**
     * 获取C3P0数据源
     *
     * @return 数据源
     */
    public static DataSource getDataSource() {
        return C3P0.getDataSource("/jdbc.properties");
    }
}