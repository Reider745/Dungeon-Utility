package com.reider.dungeonutility.struct.generation.types;

import java.util.Random;

import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public interface IStructurePiece {
    void addGeneration(IGenerationDescription stru);
    void registerType(IGenerationType type);
    void generation(int x, int z, Random random, int dimension);
    void spawnStructure(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension);
}
