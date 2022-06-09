package com.reider.dungeonutility.struct.structures;

import java.util.ArrayList;
import java.util.HashMap;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

public class StructureDestructibility {
    HashMap<Integer, ArrayList<BlockData>> map = new HashMap<>();
    public StructureDestructibility addBlock(int id, BlockState state){
        Integer key = new Integer(id);
        if(map.get(key) == null)
            map.put(key, new ArrayList<>());
        map.get(key).add(BlockData.createData(0, 0 , 0, state));
        return this;
    }
    public HashMap<Integer, ArrayList<BlockData>> getMap(){
        return map;
    }
}
