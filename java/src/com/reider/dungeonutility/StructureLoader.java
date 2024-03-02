package com.reider.dungeonutility;

import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.runtime.Callback;
import com.zhekasmirnov.innercore.api.unlimited.IDRegistry;
import com.zhekasmirnov.innercore.utils.FileTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class StructureLoader {
    public static void debugStructureFormat(String path, LoaderTypeInterface loader, StructureDescription structure) throws Exception{
        String file = loader.save(structure);
        FileTools.writeFileText(path, file);
        StructureDescription result = loader.read(file, path);
        FileTools.writeFileText(path+".result.stru", loader.save(result));
    }

    public static void debugFormats(String path){
        long start = System.currentTimeMillis();
        
        BlockData[] blocks = new BlockData[5];

        blocks[0] = BlockData.createData(0, 0, 0, new BlockState(1, 0));
        blocks[1] = BlockData.createData(-1, 0, 0, new BlockState(1, 0));
        blocks[2] = BlockData.createData(1, 0, 0, new BlockState(1, 0));
        blocks[3] = BlockData.createData(0, 1, 0, new BlockState(1, 0));
        blocks[4] = BlockData.createData(0, 2, 0, new BlockState(54, 0), new BlockState(9, 0));

        StructureDescription structure = new StructureDescription(blocks);

        types.forEach(new BiConsumer<String,LoaderTypeInterface>() {
            @Override
            public void accept(String t, LoaderTypeInterface u) {
                try {
                    debugStructureFormat(path+t+".stru", u, structure);
                } catch (Exception e) {
                    Logger.error(logger_name, "Failed debug format "+t+"\n"+ICLog.getStackTrace(e));
                }
            }
        });
        Logger.debug(logger_name, "End debug structure format - "+(System.currentTimeMillis()-start));
    }

    public static HashMap<String, StructurePool> pools = new HashMap<>();
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
        Object value = BlockID.get(id, BlockID);
        if(value instanceof Number)
            return ((Number) value).intValue();
        Logger.debug("Error get id: "+id);
        return 0;
    }
    public static Object getIdBlock(int id){
        if(id > 8000)
            return IDRegistry.getNameByID(id);
        return id;
    }
    public static int getInt(String integer){
        if(integer.equals(""))
            return 0;
        return Integer.parseInt(integer);
    }

    public static HashMap<String, Integer> getHashMapToJson(JSONObject object) throws JSONException{
        HashMap<String, Integer> result = new HashMap<>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();

            result.put(key, object.getInt(key));
        }     
        return result;
    }

    public static JSONObject getJsonForHashMap(Map<String, Integer> object) throws JSONException{
        JSONObject result = new JSONObject();
        Iterator<String> keys = object.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();

            result.put(key, object.get(key));
        }     
        return result;
    }


    public static String[] split(String str, char symbol){
        ArrayList<String> list = new ArrayList<>();
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
        HashMap<String, String[]> result = new HashMap<>();
        String[] keys = getAllPool();
        for(String name : keys)
            result.put(name, getStructurePoolByName(name).getAllStructure());
        return result;
    }

    static {
        new StructurePool(default_pool, true);
        IDRegistry.injectAPI(ids);
        BlockID = (Scriptable) ids.get("BlockID");
    }

    private static boolean stopTick = false;
    public static boolean isStopTick(){
        return stopTick;
    }

    public static void loadedStructure(){
        stopTick = true;
        Callback.invokeCallback("StructurePreLoad");
        Logger.debug(logger_name, "start load");
        long startLoad = System.currentTimeMillis();;
        for(int i = 0;i < preLoads.size();i++){
            StructurePreLoad data = preLoads.get(i);
            try {
                long start = System.currentTimeMillis();
                getStructurePoolByName(data.pool).loadRuntime(data.name, data.path, data.type, data.compile);
                Logger.debug(logger_name, "load: "+data.name+", type: "+data.type+", time: "+(System.currentTimeMillis()-start));
            }catch (Exception e){
                Logger.debug(logger_name, "failed load "+data.name+"\n"+ICLog.getStackTrace(e));
            }

        }
        Logger.debug(logger_name, "end load, load time: "+(System.currentTimeMillis()-startLoad));
        stopTick = false;
        Callback.invokeCallback("StructureLoad");
    }

    private static class StructurePreLoad {

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
                Logger.debug(logger_name, "load: "+name+", type: "+type+", time: "+((new Date().getTime())-start.getTime()));
            }catch (Exception e){
                preLoads.add(new StructurePreLoad(pool, name, path, type, compile));
                Logger.info(logger_name, e.getMessage());
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
