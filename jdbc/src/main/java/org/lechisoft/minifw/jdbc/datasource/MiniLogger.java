package org.lechisoft.minifw.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MiniLogger {
    public static Logger getLogger(){
        return LoggerFactory.getLogger("org.lechisoft.minifw.jdbc");
    }
}