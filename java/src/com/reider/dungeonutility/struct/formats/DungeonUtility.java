package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DungeonUtility implements LoaderTypeInterface {
    private static char symbol = '.';

    public static BlockData parseBlock(JSONArray list) throws JSONException{
        String[] datas = StructureLoader.split(list.getString(0), symbol);

        BlockState state = null;
        if(list.length() >= 2 && !(list.get(1) instanceof Number))
            state = new BlockState(StructureLoader.getIdBlock(datas[0]), StructureLoader.getHashMapToJson(list.getJSONObject(1)));
        else if(list.length() >= 2 && list.get(1) instanceof Number)
            state = new BlockState(StructureLoader.getIdBlock(datas[0]), ((Number) list.get(1)).intValue());
        else if(list.length() == 1)
            state = new BlockState(StructureLoader.getIdBlock(datas[0]), 0);

        BlockState state_extra = null;
        if(list.length() >= 3 && !(list.get(2) instanceof Number))
            state_extra = new BlockState(StructureLoader.getIdBlock(datas[1]), StructureLoader.getHashMapToJson(list.getJSONObject(2)));
        else if(list.length() >= 3 && list.get(2) instanceof Number)
            state_extra = new BlockState(StructureLoader.getIdBlock(datas[1]), ((Number) list.get(2)).intValue());

        NativeCompoundTag tag = null;
        if(list.length() >= 4)
            tag = CompoundTagJson.parse(list.getJSONObject(3));
        
        return BlockData.createData(
            StructureLoader.getInt(datas[2]),
            StructureLoader.getInt(datas[3]),
            StructureLoader.getInt(datas[4]),
            state,
            state_extra,
            tag
        );
    }

    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList<>();
        
        try{
            JSONArray json = new JSONArray(file);
            for(int i = 0;i < json.length();i++)
                blocks.add(parseBlock(json.getJSONArray(i)));
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

                JSONArray datas = new JSONArray();
                datas.put(str);
                if(data.state.getNamedStates().size() != 0)
                    datas.put(StructureLoader.getJsonForHashMap(data.state.getNamedStates()));

                if(data.stateExtra.getNamedStates().size() != 0){
                    if(datas.length() == 1)
                        datas.put(0);
                    datas.put(data.stateExtra.getNamedStates());
                }
                if(block.tag != null){
                    if(datas.length() == 1)
                        datas.put(0);
                    if(datas.length() == 2)
                        datas.put(0);
                    datas.put(CompoundTagJson.getMapTag(data.tag));
                }
                
                json.put(datas);
            }
        }catch (JSONException e) {
            Logger.debug(StructureLoader.logger_name, ICLog.getStackTrace(e));
        }
        return json.toString();
    }
}
