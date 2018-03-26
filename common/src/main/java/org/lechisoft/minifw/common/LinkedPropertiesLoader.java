package org.lechisoft.minifw.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 属性文件加载器
 */
public class LinkedPropertiesLoader {

    private Map<String, LinkedPropertiesFile> mapProps;


    /**
     * 构造函数
     */
    public LinkedPropertiesLoader() {
        this.mapProps = new HashMap<>();
    }

    /**
     * 加载属性文件，保存并返回Properties对象
     *
     * @param path 文件路径，如：/abc.properties
     * @return Properties对象
     */
    public LinkedProperties load(String path) {
        LinkedPropertiesFile lpf = new LinkedPropertiesFile(path);
        this.mapProps.put(lpf.getFile().getName(), lpf);
        return lpf.getLinkedProperties();
    }

    /**
     * 根据属性文件的名称获取已加载的Properties对象
     *
     * @param name 属性文件名称
     * @return Properties对象
     */
    public LinkedProperties get(String name) {
        this.refresh();
        return this.mapProps.get(name).getLinkedProperties();
    }


    /**
     * 获取指定键的值
     *
     * @param key 键
     * @return 值
     */
    public String getProperty(String key) {
        this.refresh();
        for (LinkedPropertiesFile lpf : this.mapProps.values()) {
            String value = lpf.getLinkedProperties().getProperty(key, "");
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
        this.refresh();
        for (LinkedPropertiesFile lpf : this.mapProps.values()) {
            String value = lpf.getLinkedProperties().getProperty(key, "");
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
        this.refresh();

        LinkedPropertiesFile lpf = this.mapProps.get(name);
        String value = lpf.getLinkedProperties().getProperty(key, "");
        if (!"".equals(value)) {
            return value;
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
        this.refresh();

        LinkedPropertiesFile lpf = this.mapProps.get(name);
        String value = lpf.getLinkedProperties().getProperty(key, "");
        if (!"".equals(value)) {
            return String.format(value, args);
        }
        return "";
    }

    /**
     * 如果已加载的属性文件发生变化，则重新加载
     */
    public void refresh(){
        for (LinkedPropertiesFile lpf : this.mapProps.values()) {
            if (lpf.getLastModified() != lpf.getFile().lastModified()) {
                this.load(lpf.getFileClassPath());
            }
        }
    }
}