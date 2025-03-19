package com.reider.dungeonutility.struct.generation.stand.surface;

import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.Structure;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class SurfaceTowerStand extends SurfaceStand {
    public static final String ID = "surface_tower";

    public SurfaceTowerStand(Structure structure) {
        super(structure);
    }

    @Override
    protected void placeBlockProcess(NativeBlockSource region, int x, int y, int z, BlockData block, int sx, int sz) {
        forEach(x + sx, y + this.startBuildStand, z + sz, region, offset -> {
            BlockData data = getBlockFromOffset(offset);
            if(data != null) {
                data.setCoordsNotMath(x + sx, y - offset + this.startBuildStand, z + sz, region);
                return true;
            }
            return false;
        });
    }
}
