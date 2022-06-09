package com.reider.dungeonutility.struct.formats;

import com.google.gson_du.Gson;
import com.google.gson_du.internal.LinkedTreeMap;
import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public class Structures implements LoaderTypeInterface {
    private static class StructureJson {
        public int version;
        public Object[][] structure;
    }
    private static class ArrayListStructures {
        public int version;
        public ArrayList<ArrayList<Object>> structure;
    }
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList();
        Gson gson = new Gson();
        StructureJson json = gson.fromJson(file, StructureJson.class);
        for(Object[] list : json.structure){
            BlockState state = null;
            if(list[3] instanceof Double)
                state = new BlockState(((Double) list[3]).intValue(), 0);
            else if(list[3] instanceof String)
                state = new BlockState(StructureLoader.getIdBlock((String) list[3]), 0);
            else if(list[3] instanceof LinkedTreeMap) {
                LinkedTreeMap<String, Object> obj = ((LinkedTreeMap<String, Object>) list[3]);
                Object id = obj.get("id");
                if (id instanceof Double)
                    state = new BlockState(((Double) id).intValue(), ((Double) obj.get("data")).intValue());
                else
                    state = new BlockState(StructureLoader.getIdBlock((String) id), ((Double) obj.get("data")).intValue());
            }
            blocks.add(BlockData.createData(
                    ((Double) list[0]).intValue(),
                    ((Double) list[1]).intValue(),
                    ((Double) list[2]).intValue(),
                    state
            ));
        }
        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public String save(StructureDescription stru) {
        ArrayList<ArrayList<Object>> list = new ArrayList();
        for(BlockData block : stru.blocks){
            BlockData data = block.getData();
            ArrayList<Object> datas = new ArrayList();
            datas.add(data.x);
            datas.add(data.y);
            datas.add(data.z);
            HashMap<String, Object> block_data = new HashMap();
            block_data.put("id", StructureLoader.getIdBlock(data.state.id));
            block_data.put("data", data.state.data);
            datas.add(block_data);
            datas.add(null);
            list.add(datas);
        }
        ArrayListStructures structure = new ArrayListStructures();
        structure.version = 3;
        structure.structure = list;
        Gson gson = new Gson();
        return gson.toJson(structure);
    }
}
