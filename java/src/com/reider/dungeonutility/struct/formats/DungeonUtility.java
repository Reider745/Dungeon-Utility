package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.legacy.BlockPalette;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class DungeonUtility extends LoaderType {
    private static final char symbol = '.';

    public static BlockData parseBlock(JSONArray list) throws JSONException{
        final String[] datas = Utils.split(list.getString(0), symbol);

        BlockState state = null;
        if(list.length() >= 2 && !(list.get(1) instanceof Number))
            state = StateManager.buildBlockState(
                    Utils.getIdBlock(datas[0]),
                    list.getJSONObject(1)
            );
        else if(list.length() >= 2 && list.get(1) instanceof Number)
            state = StateManager.buildBlockState(Utils.getIdBlock(datas[0]), ((Number) list.get(1)).intValue());
        else if(list.length() == 1)
            state = StateManager.buildBlockState(Utils.getIdBlock(datas[0]), 0);

        BlockState state_extra = null;
        if(list.length() >= 3 && !(list.get(2) instanceof Number))
            state_extra = StateManager.buildBlockState(
                    Utils.getIdBlock(datas[1]),
                    list.getJSONObject(2)
            );
        else if(list.length() >= 3 && list.get(2) instanceof Number)
            state_extra = StateManager.buildBlockState(
                    Utils.getIdBlock(datas[1]),
                    ((Number) list.get(2)).intValue()
            );

        NativeCompoundTag tag = null;
        if(list.length() >= 4)
            tag = CompoundTagJson.parse(list.getJSONObject(3));
        
        return BlockData.createData(
                Utils.getInt(datas[2]),
                Utils.getInt(datas[3]),
                Utils.getInt(datas[4]),
            state,
            state_extra,
            tag
        );
    }

    @Override
    public StructureDescription read(byte[] bytes, String path, BlockPalette palette) {
        final ArrayList<BlockData> blocks = new ArrayList<>();
        
        try{
            final JSONArray json = new JSONArray(new String(bytes));
            for(int i = 0;i < json.length();i++)
                blocks.add(parseBlock(json.getJSONArray(i)));
        }catch (JSONException e) {
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return new StructureDescription(blocks);
    }

    @Override
    public boolean isLoadRuntime() {
        return false;
    }

    @Override
    public byte[] save(StructureDescription stru) {
        final JSONArray json = new JSONArray();
        try{
            for(BlockData block : stru.blocks){
                final BlockData data = block.getData();

                String str = "";
                if(data.state.id != 0)
                    str+=Utils.getIdBlock(data.state.id)+".";
                else
                    str+=".";
                if(data.stateExtra.id != 0)
                    str+=Utils.getIdBlock(data.stateExtra.id)+".";
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

                final JSONArray datas = new JSONArray();
                datas.put(str);
                if(!data.state.getNamedStates().isEmpty())
                    datas.put(Utils.getJsonForHashMap(data.state.getNamedStates()));

                if(!data.stateExtra.getNamedStates().isEmpty()){
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
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
        }

        return json.toString().getBytes();
    }
}
