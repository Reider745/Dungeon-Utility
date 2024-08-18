package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class DungeonAPI_V2 extends LoaderType {
    @Override
    public StructureDescription read(byte[] bytes, String path) {
        final ArrayList<BlockData> blocks = new ArrayList<>();
        try {
            final JSONArray json = new JSONArray(new String(bytes));

            for(int i = 0;i < json.length();i++){
                final String block = json.optString(i);
                final String[] datas = block.split("\\.");

                blocks.add(BlockData.createData(Integer.parseInt(datas[2]), Integer.parseInt(datas[3]), Integer.parseInt(datas[4]),
                        StateManager.buildBlockState(Utils.getIdBlock(datas[0]), Integer.parseInt(datas[1]))));
            }
        } catch (JSONException e) {
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        
        return new StructureDescription(blocks);
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public byte[] save(StructureDescription stru) {
        String json = "[";
        for(int i = 0;i < stru.blocks.length;i++){
            final BlockData data = stru.blocks[i].getData();
            if(i != 0)
                json += ",";
            json += "\""+ Utils.getIdBlock(data.state.id) + "." + data.state.data + "." + data.x + "." + data.y + "." + data.z+"\"";
        }
        json += "]";
        return json.getBytes();
    }
}
