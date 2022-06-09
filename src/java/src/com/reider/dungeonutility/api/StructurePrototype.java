package com.reider.dungeonutility.api;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class StructurePrototype implements StructurePrototypeInterface {
    @Override
    public void before(int x, int y, int z, NativeBlockSource region, Object packet){

    }
    @Override
    public Boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        return true;
    }
    @Override
    public void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){

    }
    @Override
    public void after(int x, int y, int z, NativeBlockSource region, Object packet){

    }
}
