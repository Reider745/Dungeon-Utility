package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.Structure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public interface IGenerationDescription {
    Structure getStructure();
    void setStructure(StructureDescription description);
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
    long getTimeClearToMembory();
    int[] getCount();
    int[] getMinAndMaxY();
    boolean canClearStructure();
    boolean canLegacyOffset();
    String getUniqueIdentifier();
    String getStandName();
}
