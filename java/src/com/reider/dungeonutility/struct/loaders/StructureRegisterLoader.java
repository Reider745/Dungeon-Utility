package com.reider.dungeonutility.struct.loaders;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.util.ArrayList;

class StructureRegisterLoader {
    private static class EntryLoader {
        public String name;
        public String path;
        public String type;
        public boolean compression;

        private EntryLoader(String name, String path, String type, boolean compression){
            this.name = name;
            this.path = path;
            this.type = type;
            this.compression = compression;
        }
    }

    private final StructurePool pool;
    private final ArrayList<EntryLoader> entrys = new ArrayList<>();

    public StructureRegisterLoader(StructurePool pool){
        this.pool = pool;
    }

    public void add(String name, String path, String type, boolean compression) {
        entrys.add(new EntryLoader(name, path, type, compression));
    }

    public void loaded(){
        for(EntryLoader entry : entrys) {
            try {
                long start = System.currentTimeMillis();
                pool.loadRuntime(entry.name, entry.path, entry.type, entry.compression);
                Logger.debug(DungeonUtilityMain.logger_name, "load: " + entry.name + ", type: " + entry.type + ", time: " + (System.currentTimeMillis() - start));
            } catch (Exception e) {
                Logger.debug(DungeonUtilityMain.logger_name, "failed load " + entry.name + "\n" + ICLog.getStackTrace(e));
            }
        }
    }
}
