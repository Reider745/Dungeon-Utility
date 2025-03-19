package com.reider.dungeonutility.struct.generation.stand.surface;

import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.stand.api.BaseStand;

public abstract class SurfaceStand extends BaseStand {
    private int dirtCount = 2;
    private BlockData dirt;
    private BlockData base;
    private BlockData grass;

    protected SurfaceStand(Structure structure) {
        super(structure);
    }

    public void setDirt(BlockData dirt, int count) {
        this.dirt = dirt;
        this.dirtCount = count;
    }

    public void setBase(BlockData base) {
        this.base = base;
    }

    public void setGrass(BlockData grass) {
        this.grass = grass;
    }

    @Override
    protected boolean isFindLowBlock() {
        return false;
    }

    protected BlockData getBlockFromOffset(int offset) {
        BlockData result = null;
        if(offset == 1)
            result = grass;

        if((result == null && offset <= dirtCount + 1 ) || (result == null && offset == 1))
            result = dirt;

        if(result == null)
            return base;
        return result;
    }
}
