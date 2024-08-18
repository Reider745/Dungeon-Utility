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

public class DungeonCore extends LoaderType {
    private static char symbol = '.';

    @Override
    public StructureDescription read(byte[] bytes, String path) {
        final ArrayList<BlockData> blocks = new ArrayList<>();
        
        try{
            final JSONArray json = new JSONArray(new String(bytes));
            for(int i = 0;i < json.length();i++){
                final JSONArray list = json.getJSONArray(i);
                final String[] datas = Utils.split(list.getString(1), symbol);
                BlockState state = null;
                BlockState stateExtra = null;

                Object value = json.get(0);
                if(value instanceof Number)
                    state = StateManager.buildBlockState(
                            ((Number) value).intValue(),
                            list.getJSONObject(2)
                    );
                else if(value instanceof String)
                    state = StateManager.buildBlockState(
                            Utils.getIdBlock((String) value),
                            list.getJSONObject(2)
                    );

                if(list.length() >= 4) {
                    JSONArray extra = list.getJSONArray(3);
                    value = extra.get(0);

                    if(value instanceof Number)
                        stateExtra = StateManager.buildBlockState(
                                ((Number) value).intValue(),
                                extra.getJSONObject(1)
                        );
                    else if(extra.get(0) instanceof String)
                        stateExtra = StateManager.buildBlockState(
                                Utils.getIdBlock((String) extra.get(0)),
                                extra.getJSONObject(1));

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
            for (BlockData bl : stru.blocks){
                final BlockData data = bl.getData();
                final JSONArray list = new JSONArray();

                list.put(Utils.getIdBlock(data.state.id));
                list.put(data.state.data+"."+data.x+"."+ data.y+"."+ data.z);
                list.put(Utils.getJsonForHashMap(data.state.getNamedStates()));

                if(bl.stateExtra != null){
                    final JSONArray extra = new JSONArray();

                    extra.put(Utils.getIdBlock(data.stateExtra.id));
                    extra.put(Utils.getJsonForHashMap(data.stateExtra.getNamedStates()));

                    list.put(extra);
                }

                json.put(list);
            }
        }catch (JSONException e) {
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
        }
        
        return json.toString().getBytes();
    }
}
