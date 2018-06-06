package de.tub.secureService.service;

import org.apache.log4j.Logger;

public class Log4jUtil {
    private static final Logger log =Logger.getLogger(Log4jUtil.class.getSimpleName());

    public Log4jUtil() {
    }
    public static Logger getLogger(){
        return log;
    }
}
