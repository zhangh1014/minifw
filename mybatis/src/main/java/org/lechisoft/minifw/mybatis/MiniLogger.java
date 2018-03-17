package org.lechisoft.minifw.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MiniLogger {
    public static Logger getLogger(){
        return LoggerFactory.getLogger("org.lechisoft.minifw.mybatis");
    }
}