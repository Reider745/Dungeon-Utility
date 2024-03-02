package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class DungeonAPI_V2 implements LoaderTypeInterface {
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(file);
            for(int i = 0;i < json.length();i++){
                String block = json.optString(i);
                String[] datas = block.split("\\.");
                blocks.add(BlockData.createData(Integer.parseInt(datas[2]), Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), new BlockState(StructureLoader.getIdBlock(datas[0]), Integer.parseInt(datas[1]))));
            }
        } catch (JSONException e) {
            Logger.debug(StructureLoader.logger_name, ICLog.getStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        
        return new StructureDescription(StructureUtility.getBlocksByArrayList(blocks));
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public String save(StructureDescription stru) {
        String json = "[";
        for(int i = 0;i < stru.blocks.length;i++){
            BlockData data = stru.blocks[i].getData();
            if(i != 0)
                json += ",";
            json += "\""+ StructureLoader.getIdBlock(data.state.id) + "." + data.state.data + "." + data.x + "." + data.y + "." + data.z+"\"";
        }
        json += "]";
        return json;
    }
}
