package com.reider.dungeonutility.api.data;

import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class BlockDataState extends BlockData{
    public BlockDataState(int x, int y, int z, BlockState state){
        super(x, y, z, state);
    }

    @Override
    public void set(int X, int Y, int Z, NativeBlockSource region) {
        region.setBlock(X + x, Y + y, Z + z, state);
    }

    @Override
    public boolean isBlock(int X, int Y, int Z, NativeBlockSource region) {
        return region.getBlock(X + x, Y + y, Z + z).equals(state);
    }

    @Override
    public String getName() {
        return "BlockDataState";
    }
}
