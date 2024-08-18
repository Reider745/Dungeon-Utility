package com.reider.dungeonutility.api;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class StateManager {
    private static final ConcurrentHashMap<String, BlockState> states_cache = new ConcurrentHashMap<>();
    private static final Collection<BlockState> statesByRuntimeId = new HashSet<>();

    private static boolean isLevelLoaded = false;

    public static void init(){
        IPackVersion version = DungeonUtilityMain.getPackVersionApi();

        version.addCallback("LevelPreLoaded", args -> {
            isLevelLoaded = true;
            return null;
        }, 100);

        version.addCallback("LevelLeft", args -> {
            isLevelLoaded = false;
            statesByRuntimeId.clear();
            return null;
        }, 100);
    }

    public static final BlockState EMPTY_STATE = new BlockState(0, 0);

    public static BlockState buildBlockState(int id, int data){
        return states_cache.computeIfAbsent(id+":"+data, key -> new BlockState(id, data));
    }

    public static BlockState buildBlockState(int id, JSONObject json) throws JSONException {
        return buildBlockState(id, Utils.getHashMapToJson(json));
    }

    public static BlockState buildBlockState(int id, HashMap<String, Integer> states){
        if(!isLevelLoaded) throw new RuntimeException("Not is level loaded");

        for(BlockState state : statesByRuntimeId){
            if(state.id == id && state.getNamedStates().equals(states)){
                return state;
            }
        }

        final BlockState result = new BlockState(id, states);
        statesByRuntimeId.add(result);
        return result;
    }
}
