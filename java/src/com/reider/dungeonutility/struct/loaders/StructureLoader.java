package com.reider.dungeonutility.struct.loaders;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.runtime.Callback;

import java.util.*;

public class StructureLoader {
    private static final HashMap<String, StructurePool> pools = new HashMap<>();
    private static boolean stopTick = false;
    public static final StructurePool default_pool = new StructurePool("default");


    static {
        final IPackVersion version = DungeonUtilityMain.getPackVersionApi();

        version.addCallback("tick", args -> {
            while (stopTick){
                Thread.yield();
            }
            return null;
        });

        version.addCallback("GenerateChunkUniversal", args -> {
            while (stopTick){
                Thread.yield();
            }
            return null;
        });

        version.addCallback("LevelPreLoaded", args -> {
            stopTick = true;
            final long startLoad = System.currentTimeMillis();

            Callback.invokeCallback("StructurePreLoad");
            Logger.debug(DungeonUtilityMain.logger_name, "start load");

            for(StructurePool pool : pools.values())
                pool.getLoader().loaded();

            Logger.debug(DungeonUtilityMain.logger_name, "end load, load time: "+(System.currentTimeMillis()-startLoad));
            Callback.invokeCallback("StructureLoad");

            stopTick = false;
            return null;
        });
    }

    public static StructurePool getStructurePoolByName(String name){
        return pools.get(name);
    }

    public static StructurePool getStructurePool(String name){
        if(pools.containsKey(name))
            return getStructurePoolByName(name);
        return new StructurePool(name);
    }

    private static final String[] EMPTY_STRING = new String[0];
    public static String[] getAllPool(){
        return pools.keySet().toArray(EMPTY_STRING);
    }

    public static HashMap<String, String[]> getAllStructureAndPool(){
        HashMap<String, String[]> result = new HashMap<>();
        String[] keys = getAllPool();
        for(String name : keys)
            result.put(name, getStructurePoolByName(name).getAllStructure());
        return result;
    }

    public static void registerPool(StructurePool pool){
        pools.put(pool.getName(), pool);
    }

    @Deprecated
    public static void load(String name, String path, String type, boolean compile) {
        default_pool.load(name, path, type, compile);
    }

    @Deprecated
    public static void loadRuntime(String name, String path, String type, boolean compile){
        default_pool.loadRuntime(name, path, type, compile);
    }

    @Deprecated
    public static void loadRuntime(String name, StructureDescription stru){
        default_pool.setStructure(name, stru);
    }

    @Deprecated
    public static StructureDescription getStructure(String name){
        return default_pool.getStructure(name);
    }

    @Deprecated
    public static boolean isStructureLoad(String name){
        return default_pool.isLoad(name);
    }

    @Deprecated
    public static String[] getAllStructureName(){
        return default_pool.getAllStructure();
    }

    @Deprecated
    public static void deLoad(String name){
        default_pool.deLoad(name);
    }
}
