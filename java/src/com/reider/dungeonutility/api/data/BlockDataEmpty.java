package com.reider.dungeonutility.api.data;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class BlockDataEmpty extends BlockData{
    public BlockDataEmpty() {
        super(0, 0, 0);
    }
    public BlockDataEmpty(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public void set(int X, int Y, int Z, NativeBlockSource region) {}

    @Override
    public boolean isBlock(int X, int Y, int Z, NativeBlockSource region) {
        return true;
    }

    @Override
    public String getName() {
        return "BlockDataEmpty";
    }
}
