package com.reider.dungeonutility.struct.formats;

import com.google.gson_du.Gson;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DungeonUtility_V2 implements LoaderTypeInterface {
    private static class BlockExtra {
        public int id;
        public Map<String, Integer> state;
    }
    private static class BlockBase {
        public int id;
        public Map<String, Integer> state;
        public BlockExtra extra;
    }
    private static class StructureBase {
        public int version;
        public Map<String, BlockBase> blocks;
        public String[][] positions;
    }
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList();
        Gson gson = new Gson();
        StructureBase base = gson.fromJson(file, StructureBase.class);
        if(base.version == 1){
            for(int y = 0;y < base.positions.length;y++){
                
            }
        }
        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return false;
    }

    @Override
    public String save(StructureDescription stru) {
        return null;
    }
}
