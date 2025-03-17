package com.reider.dungeonutility.struct.formats.legacy;

import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.Utils;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BlockPalette {
    public static final BlockPalette DEFAULT = new BlockPalette();

    private final HashMap<String, String> ids;
    private IConvertStates convertStates = (old_id, id, states) -> states;

    public interface IConvertStates {
        HashMap<String, Integer> convert(String old_id, String id, HashMap<String, Integer> states);
    }

    public BlockPalette(HashMap<String, String> ids) {
        this.ids = ids;
    }

    public BlockPalette() {
        this(new HashMap<>());
    }

    public void add(String originalId, String newId) {
        ids.put(originalId, newId);
    }

    public void setConvertStates(IConvertStates convertStates) {
        this.convertStates = convertStates;
    }

    public BlockState buildBlockState(int num_id, int meta) {
        if(num_id > 8000)
            return StateManager.buildBlockState(num_id, meta);
        return StateManager.buildBlockState(num_id, meta);
    }

    public BlockState buildBlockState(String id, int meta) {
        final int num_id = Utils.getIdBlock(id);
        if (num_id > 8000)
            return buildBlockState(num_id, meta);
        return buildBlockState(num_id, meta);
    }

    public BlockState buildBlockState(int id, JSONObject json) {
        try {
            return StateManager.buildBlockState(id, Utils.getHashMapToJson(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public BlockState buildBlockState(String id, JSONObject json) {
        try {
            return buildBlockState(id, Utils.getHashMapToJson(json));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public BlockState buildBlockState(String id, HashMap<String, Integer> states) {
        for (String key_id : ids.keySet()) {
            if(key_id.equals(id)) {
                String old_id = id;
                id = ids.get(key_id);
                states = convertStates.convert(old_id, id, states);
                break;
            }
        }

        return StateManager.buildBlockState(Utils.getIdBlock(id), states);
    }
}
