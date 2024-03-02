package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Structures implements LoaderTypeInterface {
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList<>();

        try{
            JSONObject json = new JSONObject(file);
            JSONArray structure = json.getJSONArray("structure");
            for(int i = 0;i < structure.length();i++){
                JSONArray list = structure.getJSONArray(i);
                
                Object value = list.get(3);
                BlockState state = null;
                if(value instanceof Number)
                    state = new BlockState(((Number) value).intValue(), 0);
                else if(value instanceof String)
                    state = new BlockState(StructureLoader.getIdBlock((String) value), 0);
                else if(value instanceof JSONObject) {
                    JSONObject obj = ((JSONObject) value);
                    Object id = obj.get("id");
                    if (id instanceof Number)
                        state = new BlockState(((Number) id).intValue(), ((Number) obj.get("data")).intValue());
                    else
                        state = new BlockState(StructureLoader.getIdBlock((String) id), ((Double) obj.get("data")).intValue());
                }

                blocks.add(BlockData.createData(
                        list.getInt(0),
                        list.getInt(1),
                        list.getInt(2),
                        state
                ));
            }
        }catch (JSONException e) {
            Logger.debug(StructureLoader.logger_name, ICLog.getStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public String save(StructureDescription stru) {
        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();

        try{
            for(BlockData block : stru.blocks){
                BlockData data = block.getData();
                JSONArray datas = new JSONArray();
                datas.put(data.x);
                datas.put(data.y);
                datas.put(data.z);

                JSONObject block_data = new JSONObject();
                block_data.put("id", StructureLoader.getIdBlock(data.state.id));
                block_data.put("data", data.state.data);

                datas.put(block_data);
                datas.put(null);
                list.put(datas);
            }

            json.put("version", 3);
            json.put("structure", list);
        }catch (JSONException e) {
            Logger.debug(StructureLoader.logger_name, ICLog.getStackTrace(e));
        }

        return json.toString();
    }
}
