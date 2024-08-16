package com.reider.dungeonutility.logger;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

public class Debug {
    private static ILogger logger = new LoggerEnable();

    public static ILogger get(){
        return logger;
    }

    public static void set(ILogger log){
        Logger.debug(DungeonUtilityMain.logger_name,"update logger: "+log.getClass().getName());
        logger.close();
        log.open();
        logger = log;
    }
}
