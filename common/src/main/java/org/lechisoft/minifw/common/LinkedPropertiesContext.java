package org.lechisoft.minifw.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性文件加载器
 */
public class LinkedPropertiesContext {

    private Map<String, LinkedPropertiesFile> mapProps;


    /**
     * 构造函数
     */
    public LinkedPropertiesContext() {
        this.mapProps = new HashMap<>();
    }

    /**
     * 添加LinkedPropertiesFile对象
     *
     * @param name 名称
     * @param obj  LinkedPropertiesFile对象
     */
    public void put(String name, LinkedPropertiesFile obj) {
        this.mapProps.put(name, obj);
    }

    /**
     * 获取的LinkedPropertiesFile对象
     *
     * @param name 名称
     * @return LinkedPropertiesFile对象
     */
    public LinkedPropertiesFile get(String name) {
        this.refresh();
        return this.mapProps.get(name);
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
        if (hasChanged(name)) {
            refresh(name);
        }

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
        if (hasChanged(name)) {
            refresh(name);
        }

        LinkedPropertiesFile lpf = this.mapProps.get(name);
        String value = lpf.getLinkedProperties().getProperty(key, "");
        if (!"".equals(value)) {
            return String.format(value, args);
        }
        return "";
    }

    /**
     * 判断指定的属性文件是否变化
     *
     * @param name 名称
     * @return 是否变化
     */
    public boolean hasChanged(String name) {
        LinkedPropertiesFile lpf = this.mapProps.get(name);
        return lpf.getLastModified() != lpf.getFile().lastModified();
    }

    /**
     * 刷新指定的属性文件
     *
     * @param name 名称
     */
    public void refresh(String name) {
        LinkedPropertiesFile lpf = this.mapProps.get(name);
        lpf = new LinkedPropertiesFile(lpf.getFileClassPath());
        this.put(name, lpf);
    }

    /**
     * 刷新所有属性文件
     */
    public void refresh() {
        for (String name : this.mapProps.keySet()) {
            if (hasChanged(name)) {
                refresh(name);
            }
        }
    }
}