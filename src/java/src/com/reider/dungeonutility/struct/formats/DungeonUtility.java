package com.reider.dungeonutility.struct.formats;

import com.google.gson_du.Gson;
import com.google.gson_du.JsonArray;
import com.google.gson_du.JsonObject;
import com.google.gson_du.internal.LinkedTreeMap;
import com.google.gson_du.reflect.TypeToken;
import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DungeonUtility implements LoaderTypeInterface {
    private static char symbol = '.';
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList();
        Gson gson = new Gson();
        Object[][] json = gson.fromJson(file, Object[][].class);
        for (Object[] list : json){
            String[] datas = StructureLoader.split((String) list[0], symbol);
            BlockState state = null;
            if(list.length >= 2 && !(list[1] instanceof Double))
                state = new BlockState(StructureLoader.getIdBlock(datas[0]), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) list[1]));
            else if(list.length >= 2 && list[1] instanceof Double)
                state = new BlockState(StructureLoader.getIdBlock(datas[0]), ((Double) list[1]).intValue());

            BlockState state_extra = null;
            if(list.length >= 3 && !(list[2] instanceof Double))
                state = new BlockState(StructureLoader.getIdBlock(datas[1]), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) list[2]));
            else if(list.length >= 3 && list[2] instanceof Double)
                state = new BlockState(StructureLoader.getIdBlock(datas[1]), ((Double) list[2]).intValue());
            blocks.add(BlockData.createData(
                    StructureLoader.getInt(datas[2]),
                    StructureLoader.getInt(datas[3]),
                    StructureLoader.getInt(datas[4]),
                    state,
                    state_extra
            ));
        }
        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return false;
    }

    @Override
    public String save(StructureDescription stru) {
        ArrayList<ArrayList<Object>> list = new ArrayList();
        for(BlockData block : stru.blocks){
            BlockData data = block.getData();
            String str = "";
            if(data.state.id != 0)
                str+=StructureLoader.getIdBlock(data.state.id)+".";
            else
                str+=".";
            if(data.stateExtra.id != 0)
                str+=StructureLoader.getIdBlock(data.stateExtra.id)+".";
            else
                str+=".";
            if(data.x != 0)
                str+=data.x+".";
            else
                str+=".";
            if(data.y != 0)
                str+=data.y+".";
            else
                str+=".";
            if(data.z != 0)
                str+=data.z;
            ArrayList<Object> datas = new ArrayList();
            datas.add(str);
            if(data.state.getNamedStates().size() != 0)
                datas.add(data.state.getNamedStates());
            if(data.stateExtra.getNamedStates().size() != 0){
                if(datas.size() == 1)
                    datas.add(0);
                datas.add(data.state.getNamedStates());
            }
            list.add(datas);
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
