package com.reider.dungeonutility.struct.generation.types;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public interface IGenerationType {
    String getType();
    boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region);
    Vector3 getPosition(int x, int z, Random random, int dimension, NativeBlockSource region);
}
