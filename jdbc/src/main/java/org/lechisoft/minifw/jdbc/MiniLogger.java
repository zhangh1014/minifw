package org.lechisoft.minifw.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiniLogger {
    public static Logger getLogger(){
        return LoggerFactory.getLogger("org.lechisoft.minifw.jdbc");
    }
}