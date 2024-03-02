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

public class DungeonCore implements LoaderTypeInterface {
    private static char symbol = '.';
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList<>();
        
        try{
            JSONArray json = new JSONArray(file);
            for(int i = 0;i < json.length();i++){
                JSONArray list = json.getJSONArray(i);
                String[] datas = StructureLoader.split(list.getString(1), symbol);
                BlockState state = null;
                BlockState stateExtra = null;

                Object value = json.get(0);
                if(value instanceof Number)
                    state = new BlockState(((Number) value).intValue(), StructureLoader.getHashMapToJson(list.getJSONObject(2)));
                else if(value instanceof String)
                    state = new BlockState(StructureLoader.getIdBlock((String) value), StructureLoader.getHashMapToJson(list.getJSONObject(2)));

                if(list.length() >= 4) {
                    JSONArray extra = list.getJSONArray(3);
                    value = extra.get(0);

                    if(value instanceof Number)
                        stateExtra = new BlockState(((Number) value).intValue(), StructureLoader.getHashMapToJson(extra.getJSONObject(1)));
                    else if(extra.get(0) instanceof String)
                        stateExtra = new BlockState(((int) (StructureLoader.BlockID.get((String) extra.get(0), StructureLoader.BlockID))), StructureLoader.getHashMapToJson(extra.getJSONObject(1)));

                }

                blocks.add(BlockData.createData(
                    Integer.parseInt(datas[1]),
                    Integer.parseInt(datas[2]),
                    Integer.parseInt(datas[3]),
                    state,
                    stateExtra
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
        return false;
    }

    @Override
    public String save(StructureDescription stru) {
        JSONArray json = new JSONArray();
        try{
            for (BlockData bl : stru.blocks){
                BlockData data = bl.getData();
                JSONArray list = new JSONArray();
                list.put(StructureLoader.getIdBlock(data.state.id));
                list.put(data.state.data+"."+data.x+"."+ data.y+"."+ data.z);
                list.put(StructureLoader.getJsonForHashMap(data.state.getNamedStates()));

                if(bl.stateExtra != null){
                    JSONArray extra = new JSONArray();
                    extra.put(StructureLoader.getIdBlock(data.stateExtra.id));
                    extra.put(StructureLoader.getJsonForHashMap(data.stateExtra.getNamedStates()));
                    list.put(extra);
                }

                json.put(list);
            }
        }catch (JSONException e) {
            Logger.debug(StructureLoader.logger_name, ICLog.getStackTrace(e));
        }
        
        return json.toString();
    }
}
