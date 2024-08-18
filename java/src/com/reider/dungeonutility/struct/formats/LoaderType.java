package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.du_v2.DungeonUtility_V2;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import java.util.HashMap;

public abstract class LoaderType {
    public abstract StructureDescription read(byte[] file, String path);
    public abstract byte[] save(StructureDescription stru);
    public abstract boolean isLoadRuntime();

    public static void debugStructureFormat(String path, LoaderType loader, StructureDescription structure) throws Exception{
        final byte[] file = loader.save(structure);
        Utils.writeFileBytes(path, file);

        StructureDescription result = loader.read(file, path);
        Utils.writeFileBytes(path+".result.stru", loader.save(result));
    }

    private static HashMap<String, LoaderType> types = new  HashMap<String, LoaderType>();
    private static final String[] EMPTY_STRING = new String[0];

    public static void debugFormats(String path){
        final long start = System.currentTimeMillis();
        final BlockData[] blocks = new BlockData[5];

        blocks[0] = BlockData.createData(0, 0, 0, new BlockState(1, 0));
        blocks[1] = BlockData.createData(-1, 0, 0, new BlockState(1, 0));
        blocks[2] = BlockData.createData(1, 0, 0, new BlockState(1, 0));
        blocks[3] = BlockData.createData(0, 1, 0, new BlockState(1, 0));
        blocks[4] = BlockData.createData(0, 2, 0, new BlockState(54, 0), new BlockState(9, 0));

        final StructureDescription structure = new StructureDescription(blocks);

        types.forEach((k, v) -> {
            try {
                debugStructureFormat(path+k+".stru", v, structure);
            } catch (Exception e) {
                Logger.error(DungeonUtilityMain.logger_name, "Failed debug format "+k+"\n"+ ICLog.getStackTrace(e));
            }
        });

        Logger.debug(DungeonUtilityMain.logger_name, "End debug structure format - "+(System.currentTimeMillis()-start));
    }

    public static void registerType(String name, LoaderType type){
        types.put(name, type);
    }
    public static LoaderType getType(String name){
        return types.get(name);
    }
    public static String[] getTypes(){
        return types.keySet().toArray(EMPTY_STRING);
    }

    static {
        registerType("DungeonAPI", new DungeonAPI());
        registerType("DungeonAPI_V2", new DungeonAPI_V2());
        registerType("DungeonCore", new DungeonCore());
        registerType("Structures", new Structures());
        registerType("DungeonUtility", new DungeonUtility());
        registerType("DU", new DungeonUtility());
        registerType("DungeonUtility_V2", new DungeonUtility_V2());
        registerType("DU2", new DungeonUtility_V2());
        registerType("nbt", new VanillaNbt());
    }
}
