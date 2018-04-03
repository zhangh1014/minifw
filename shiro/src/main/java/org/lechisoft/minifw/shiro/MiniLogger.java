package org.lechisoft.minifw.shiro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MiniLogger {
    public static Logger getLogger() {
        return MiniLogger.getLogger("org.lechisoft.minifw.shiro");
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static void debug(String s) {
        MiniLogger.getLogger().debug(" ~_~ "+s);
    }

    public static void info(String s) {
        MiniLogger.getLogger().info(" ^_^ "+s);
    }

    public static void warn(String s) {
        MiniLogger.getLogger().warn(" →_→ "+s);
    }

    public static void error(String s) {
        MiniLogger.getLogger().error(" >_< "+s);
    }
}