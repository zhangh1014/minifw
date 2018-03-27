package org.lechisoft.minifw.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 属性文件
 */
public class LinkedPropertiesFile {

    private File file;
    private LinkedProperties linkedProperties;
    private String fileClassPath;
    private long lastModified = 0;

    public LinkedPropertiesFile(String path) {
        URL url = LinkedPropertiesFile.class.getResource(path);
        if (null == url) {
            MiniLogger.error("属性文件" + path + "不存在");
        } else {

            this.file = new File(url.getPath());
            this.linkedProperties = new LinkedProperties();
            try {
                MiniLogger.debug("正在读取属性文件：" + path);
                InputStream is = new FileInputStream(file);
                this.linkedProperties.load(is);
                is.close();
                MiniLogger.debug("读取属性文件成功");

            } catch (IOException e) {
                MiniLogger.error("读取属性文件发生异常");
            }
            this.fileClassPath = path;
            this.lastModified = file.lastModified();
        }
    }

    public LinkedProperties getLinkedProperties() {
        return linkedProperties;
    }

    public File getFile() {
        return file;
    }

    public String getFileClassPath() {
        return fileClassPath;
    }

    public long getLastModified() {
        return this.lastModified;
    }
}