package com.reider.dungeonutility.api.data;

import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class BlockDataAir extends BlockData {
    public BlockDataAir(int x, int y, int z){
        super(x, y, z);
    }

    @Override
    public void set(int X, int Y, int Z, NativeBlockSource region) {
        region.setBlock(X+x,Y+y,Z+z, 0);
        region.setExtraBlock(X+x,Y+y,Z+z, 0);
    }

    @Override
    public Boolean isBlock(int X, int Y, int Z, NativeBlockSource region) {
        return region.getBlock(X + x, Y + y, Z + z).id == 0 && region.getExtraBlock(X + x, Y + y, Z + z).id == 0;
    }

    @Override
    public String getName() {
        return "BlockDataAir";
    }
}
