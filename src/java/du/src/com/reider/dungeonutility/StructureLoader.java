package com.reider.dungeonutility;

import com.google.gson_du.internal.LinkedTreeMap;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;

import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.*;
import java.util.*;

public class StructureLoader {
    public static HashMap<String, StructurePool> pools = new HashMap();
    public static final String default_pool = "default";
    public static final String logger_name = "DungeonUtility";
    public static ScriptableObject ids = new ScriptableObject() {
        @Override
        public String getClassName() {
            return "DungeonUtility";
        }
    };
    public static Scriptable BlockID;

    private static boolean structure_optimization;

    public static void setStructureOptimization(boolean structure_optimization) {
        StructureLoader.structure_optimization = structure_optimization;
    }

    public static StructurePool getStructurePoolByName(String name){
        return pools.get(name);
    }
    public static StructurePool getStructurePool(String name){
        if(pools.containsKey(name))
            return getStructurePoolByName(name);
        return new StructurePool(name);
    }
    public static String[] getAllPool(){
        return pools.keySet().toArray(new String[pools.size()]);
    }

    private static boolean isNumber(String str){
        char[] chars = str.toCharArray();
        for(char c : chars)
            if(!Character.isDigit(c))
                return false;
        return true;
    }

    public static int getIdBlock(String id){
        if(id.equals(""))
            return 0;
        if(isNumber(id))
            return Integer.parseInt(id);
        return (int) (BlockID.get(id, BlockID));
    }
    public static Object getIdBlock(int id){
        if(id > 8000)
            return AdaptedScriptAPI.IDRegistry.getNameByID(id);
        return id;
    }
    public static int getInt(String integer){
        if(integer.equals(""))
            return 0;
        return Integer.parseInt(integer);
    }
    public static HashMap<String, Integer> getHashMapToJson(LinkedTreeMap<String, Double> object){
        HashMap<String, Integer> result = new HashMap();
        Set<Map.Entry<String, Double>> keys = object.entrySet();
        for(Map.Entry<String, Double> key : keys)
            result.put(key.getKey(), key.getValue().intValue());
        return result;
    }

    public static String[] split(String str, char symbol){
        ArrayList<String> list = new ArrayList();
        list.add("");
        char[] chars = str.toCharArray();
        for(char s : chars)
            if(s == symbol)
                list.add("");
            else{
                int index = list.size() - 1;
                list.set(index, list.get(index) + s);
            }
        return list.toArray(new String[list.size()]);
    }

    public static HashMap<String, String[]> getAllStructureAndPool(){
        HashMap<String, String[]> result = new HashMap();
        String[] keys = getAllPool();
        for(String name : keys)
            result.put(name, getStructurePoolByName(name).getAllStructure());
        return result;
    }

    static {
        StructurePool pool = new StructurePool(default_pool, true);
        AdaptedScriptAPI.IDRegistry.injectAPI(ids);
        BlockID = (Scriptable) ids.get("BlockID");
    }

    private static boolean stopTick = false;
    public static boolean isStopTick(){
        return stopTick;
    }

    public static void loadedStructure(){
        stopTick = true;
        AdaptedScriptAPI.Callback.invokeCallback("StructurePreLoad", null, null, null, null, null, null, null, null, null, null);
        AdaptedScriptAPI.Logger.Log("start load", logger_name);
        long startLoad = System.currentTimeMillis();;
        for(int i = 0;i < preLoads.size();i++){
            StructurePreLoad data = preLoads.get(i);
            try {
                long start = System.currentTimeMillis();
                getStructurePoolByName(data.pool).loadRuntime(data.name, data.path, data.type, data.compile);
                AdaptedScriptAPI.Logger.Log("load: "+data.name+", type: "+data.type+", time: "+(System.currentTimeMillis()-start), logger_name);
            }catch (Exception e){
                AdaptedScriptAPI.Logger.Log("failed load "+data.name+" "+e.getMessage(), logger_name);
            }

        }
        AdaptedScriptAPI.Logger.Log("end load, load time: "+(System.currentTimeMillis()-startLoad), logger_name);
        stopTick = false;
        AdaptedScriptAPI.Callback.invokeCallback("StructureLoad", null, null, null, null, null, null, null, null, null, null);
    }

    private static class StructurePreLoad {
        public StructurePreLoad(String name, String path, String type, Boolean compile){
            this(default_pool, name, path, type, compile);
        }
        public StructurePreLoad(String pool, String name, String path, String type, Boolean compile){
            this.pool = pool;
            this.name = name;
            this.path = path;
            this.type = type;
            this.compile = compile;
        }
        public String pool;
        public String name;
        public String path;
        public String type;
        public Boolean compile;
    }

    public static String getTextToFile(String path){
        String text = "";
        try {
            Reader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while((line = br.readLine())!= null)
                text+=line;
            br.close();
        } catch (Exception e) {}
        return text;
    }

    private static HashMap<String, LoaderTypeInterface> types = new  HashMap<String, LoaderTypeInterface>();
    private static ArrayList<StructurePreLoad> preLoads = new ArrayList<StructurePreLoad>();

    public static void registerType(String name, LoaderTypeInterface type){
        types.put(name, type);
    }
    public static LoaderTypeInterface getType(String name){
        return types.get(name);
    }

    public static void load(String name, String path, String type, Boolean compile) {
        load(default_pool , name, path, type, compile);
    }
    public static void load(String pool, String name, String path, String type, Boolean compile) {
        if(getType(type).isLoadRuntime()) {
            try{
                Date start = new Date();
                getStructurePool(pool).loadRuntime(name, path, type, compile);
                AdaptedScriptAPI.Logger.Log("load: "+name+", type: "+type+", time: "+((new Date().getTime())-start.getTime()), logger_name);
            }catch (Exception e){
                preLoads.add(new StructurePreLoad(pool, name, path, type, compile));
                AdaptedScriptAPI.Logger.info(e.getMessage(), logger_name);
            }

        }else
            preLoads.add(new StructurePreLoad(pool, name, path, type, compile));
    }
    public static ArrayList<StructurePreLoad> getPreLoads(){
        return preLoads;
    }


    public static void loadRuntime(String name, String path, String type, Boolean compile){
        getStructurePoolByName(default_pool).loadRuntime(name, path, type, compile);
    }
    public static void loadRuntime(String name, StructureDescription stru){
        getStructurePoolByName(default_pool).setStructure(name, stru);
    }

    public static StructureDescription getStructure(String name){
        return getStructurePoolByName(default_pool).getStructure(name);
    }
    public static Boolean isStructureLoad(String name){
        return getStructurePoolByName(default_pool).isLoad(name);
    }
    public static String[] getAllStructureName(){
        return getStructurePoolByName(default_pool).getAllStructure();
    }
    public static void deLoad(String name){
        getStructurePoolByName(default_pool).deLoad(name);
    }
}
