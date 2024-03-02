package com.reider;

import com.reider.dungeonutility.api.type.ILogger;
import com.reider.dungeonutility.api.type.LoggerEnable;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

public class Debug {
    public static ILogger logger = new LoggerEnable();

    public static ILogger get(){
        return logger;
    }

    public static void set(ILogger log){
        Logger.debug("update logger: "+log.getClass().getName());
        logger.close();
        log.open();
        logger = log;
    }
}
