package org.lechisoft.minifw.common;

import java.io.File;

public class MiniFile {

    /**
     * 查找指定目录下的文件并返回
     *
     * @param dir      查找目录
     * @param fileName 文件名
     * @return 查找到的文件或null
     */
    public static File findFile(String dir, String fileName) {
        File file = new File(dir);
        if (!file.exists()) {
            return null;
        }
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (null != subFiles) {
                for (File subFile : subFiles) {
                    File rtnFile = findFile(subFile.getAbsolutePath(), fileName);
                    if (null != rtnFile) {
                        return rtnFile;
                    }
                }
            }
        } else if (file.isFile()) {
            if (fileName.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }
}
