package com.reider.dungeonutility.struct.formats.du_v2.util;

import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.Utils;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.innercore.api.unlimited.IDRegistry;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;

public class State {
    public static BlockState read(boolean compression, ByteBuffer buffer){
        int id;
        if(buffer.get() == 1){
            id = IDRegistry.genBlockID(Utils.readString(buffer));
        }else{
            id = buffer.getShort();
        }

        try {
            return StateManager.buildBlockState(id, new JSONObject(Utils.readString(buffer)));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static void write(boolean compression, BlockState state, ByteBuffer buffer){
        /*
        compression - сохранять только runtimeId
         */
        if(state.id > 8000){
            buffer.put((byte) 1);
            Utils.putString(buffer, IDRegistry.getNameByID(state.id));
        }else{
            buffer.put((byte) 0);
            buffer.putShort((short) state.id);
        }

        try {
            Utils.putString(buffer, Utils.getJsonForHashMap(state.getNamedStates()).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static int mathLength(boolean compression, BlockState state) {
        int count = 1;

        if(state.id > 8000){
            count += Utils.mathLength(IDRegistry.getNameByID(state.id));
        }else{
            count += 2;
        }

        try {
            count += Utils.mathLength(Utils.getJsonForHashMap(state.getNamedStates()).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    public static boolean equals(BlockState data, BlockState data2){
        if(data2 == null) data2 = StateManager.EMPTY_STATE;
        if(data == null) data = StateManager.EMPTY_STATE;
        return data.id == data2.id && data.getStates().toString().equals(data2.getStates().toString());
    }
}
