package org.lechisoft.minifw.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PropertiesLoader {

    private Map<String, LinkedProperties> mapProps;

    public PropertiesLoader() {
        this.mapProps = new HashMap<>();
    }

    private void load(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            LinkedProperties lnkProps = new LinkedProperties();
            try {
                InputStream is = new FileInputStream(file);
                lnkProps.load(is);
                this.mapProps.put(file.getName(), lnkProps);
                is.close();
                MiniLogger.getLogger().debug("已加载属性文件" + path);
            } catch (IOException e) {
                MiniLogger.getLogger().error("读取属性文件" + path + "发生异常");
            }
        } else {
            MiniLogger.getLogger().error("属性文件" + path + "不存在");
        }
    }

    /**
     * 加载属性文件
     *
     * @param paths 基于classes的文件路径，如：/abc.properties
     */
    public void load(String... paths) {
        if (paths.length > 0) {
            for (String path : paths) {
                URL url = PropertiesLoader.class.getResource(path);
                if (null == url) {
                    MiniLogger.getLogger().error("属性文件" + path + "不存在");
                } else {
                    this.load(path);
                }
            }
        }
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