package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Structures extends LoaderType {
    @Override
    public StructureDescription read(byte[] bytes, String path) {
        final ArrayList<BlockData> blocks = new ArrayList<>();

        try{
            final JSONObject json = new JSONObject(new String(bytes));
            final JSONArray structure = json.getJSONArray("structure");

            for(int i = 0;i < structure.length();i++){
                final JSONArray list = structure.getJSONArray(i);
                
                final Object value = list.get(3);
                BlockState state = null;
                if(value instanceof Number)
                    state = new BlockState(((Number) value).intValue(), 0);
                else if(value instanceof String)
                    state = new BlockState(Utils.getIdBlock((String) value), 0);
                else if(value instanceof JSONObject) {
                    final JSONObject obj = ((JSONObject) value);
                    Object id = obj.get("id");
                    if (id instanceof Number)
                        state = StateManager.buildBlockState(
                                ((Number) id).intValue(),
                                ((Number) obj.get("data")).intValue()
                        );
                    else
                        state = StateManager.buildBlockState(
                                Utils.getIdBlock((String) id),
                                ((Number) obj.get("data")).intValue()
                        );
                }

                blocks.add(BlockData.createData(
                        list.getInt(0),
                        list.getInt(1),
                        list.getInt(2),
                        state
                ));
            }
        }catch (JSONException e) {
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
        final JSONArray list = new JSONArray();
        final JSONObject json = new JSONObject();

        try{
            for(BlockData block : stru.blocks){
                final BlockData data = block.getData();
                final JSONArray datas = new JSONArray();

                datas.put(data.x);
                datas.put(data.y);
                datas.put(data.z);

                final JSONObject block_data = new JSONObject();
                block_data.put("id", Utils.getIdBlock(data.state.id));
                block_data.put("data", data.state.data);

                datas.put(block_data);
                datas.put(null);

                list.put(datas);
            }

            json.put("version", 3);
            json.put("structure", list);
        }catch (JSONException e) {
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
        }

        return json.toString().getBytes();
    }
}
