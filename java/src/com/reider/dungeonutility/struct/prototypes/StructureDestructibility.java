package com.reider.dungeonutility.struct.prototypes;

import java.util.ArrayList;
import java.util.HashMap;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

public class StructureDestructibility {
    private final HashMap<Integer, ArrayList<BlockData>> map = new HashMap<>();

    public StructureDestructibility addBlock(int id, BlockState state){
        ArrayList<BlockData> blocks = map.get(id);
        if(blocks == null)
            blocks = new ArrayList<>();
        blocks.add(BlockData.createData(0, 0 , 0, state));
        map.put(id, new ArrayList<>());
        return this;
    }

    public HashMap<Integer, ArrayList<BlockData>> getMap(){
        return map;
    }
}
