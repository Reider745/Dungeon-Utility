package com.reider.dungeonutility.struct.generation.stand;

import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.stand.api.BaseStand;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class LastingStand extends BaseStand {
    public static final String ID = "lasting";

    public LastingStand(Structure structure) {
        super(structure);
    }

    @Override
    protected boolean isFindLowBlock() {
        return true;
    }

    @Override
    protected void placeBlockProcess(NativeBlockSource region, int x, int y, int z, BlockData block) {
        if(block != null)  {
            forEach(x + block.x, y + block.y, z + block.z, region, (offset) -> {
                block.set(x, y - offset, z, region);
                return true;
            });
        }
    }
}
