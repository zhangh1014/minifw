package org.lechisoft.minifw.jdbc;

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
        ComboPooledDataSource dataSource = null;
        Properties props = new Properties();
        try {
            props.load(C3P0.class.getResourceAsStream(path));
            dataSource = new ComboPooledDataSource();
        } catch (IOException e) {
            MiniLogger.getLogger().error("C3P0读取属性文件" + path + "发生异常");
        } catch (Exception e) {
            MiniLogger.getLogger().error("创建C3P0发生异常");
        }

        // 动态为数据源属性赋值
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
                MiniLogger.getLogger().warn("C3P0属性" + key + "不存在");
            } else {
                try {
                    m.invoke(dataSource, value);
                    MiniLogger.getLogger().debug(key + "=" + value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    MiniLogger.getLogger().warn("C3P0设置属性" + key + "发生异常");
                }
            }
        }
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
