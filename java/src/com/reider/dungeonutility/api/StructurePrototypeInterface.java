package com.reider.dungeonutility.api;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public interface StructurePrototypeInterface {
    default void before(int x, int y, int z, NativeBlockSource region, Object packet){

    }
    default boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        return true;
    }
    default void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){

    }
    default void after(int x, int y, int z, NativeBlockSource region, Object packet){

    }
}
