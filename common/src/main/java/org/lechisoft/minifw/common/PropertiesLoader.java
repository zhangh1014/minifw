package org.lechisoft.minifw.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 属性文件加载器
 */
public class PropertiesLoader {

    private Map<String, LinkedProperties> mapProps;


    /**
     * 构造函数
     */
    public PropertiesLoader() {
        this.mapProps = new HashMap<>();
    }

    /**
     * 加载属性文件，保存并返回Properties对象
     *
     * @param path 文件路径，如：/abc.properties
     * @return Properties对象
     */
    public LinkedProperties load(String path) {
        MiniLogger.debug("正在读取属性文件：" + path);
        URL url = PropertiesLoader.class.getResource(path);
        if (null == url) {
            MiniLogger.error("属性文件不存在");
            return null;
        } else {
            LinkedProperties lnkProps = new LinkedProperties();
            File file = new File(url.getPath());
            try {
                InputStream is = new FileInputStream(file);
                lnkProps.load(is);
                this.mapProps.put(file.getName(), lnkProps);
                is.close();
                MiniLogger.debug("读取属性文件成功");
                return lnkProps;
            } catch (IOException e) {
                MiniLogger.error("读取属性文件发生异常");
            }
        }
        return null;
    }

    /**
     * 根据属性文件的名称获取已加载的Properties对象
     * @param fileName 属性文件名称
     * @return Properties对象
     */
    public LinkedProperties get(String fileName) {
        return this.mapProps.get(fileName);
    }


    /**
     * 获取指定键的值
     *
     * @param key 键
     * @return 值
     */
    public String getProperty(String key) {
        for (LinkedProperties lnkProps : this.mapProps.values()) {
            String value = lnkProps.getProperty(key, "");
            if (!"".equals(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * 获取指定键的值，并进行字符串的格式化处理
     *
     * @param key  键
     * @param args 格式化参数
     * @return 值
     */
    public String getProperty(String key, Object... args) {
        for (LinkedProperties lnkProps : this.mapProps.values()) {
            String value = lnkProps.getProperty(key, "");
            if (!"".equals(value)) {
                return String.format(value, args);
            }
        }
        return "";
    }

    /**
     * 获取指定属性文件的键的值
     *
     * @param name 属性文件名称
     * @param key  键
     * @return 值
     */
    public String getProperty(String name, String key) {
        LinkedProperties lnkProps = this.mapProps.get(name);
        if (null != lnkProps) {
            String value = lnkProps.getProperty(key, "");
            if (!"".equals(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * 获取指定属性文件的键的值，并进行字符串的格式化处理
     *
     * @param name 属性文件名称
     * @param key  键
     * @param args 格式化参数
     * @return 值
     */
    public String getProperty(String name, String key, Object... args) {
        LinkedProperties lnkProps = this.mapProps.get(name);
        if (null != lnkProps) {
            String value = lnkProps.getProperty(key, "");
            if (!"".equals(value)) {
                return String.format(value, args);
            }
        }
        return "";
    }
}