package com.reider.dungeonutility.struct.generation;

import com.reider.dungeonutility.struct.Structure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public interface IGenerationDescription {
    Structure getStructure();
    int getChance();
    String getType();
    boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region);
    boolean isPoolStructure(Vector3 pos, Random random, int dimension, NativeBlockSource region);
    String getName();
    double getDistance();
    boolean isSet();
    Vector3 getOffset();
    boolean checkName();
    boolean canOptimization();
    boolean canLegacySpawn();
}
