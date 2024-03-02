package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public interface IStructureStorage {
    WorldStructure[] getStructures();
    void setStructures(WorldStructure[] structure);
    void clear();
    void add(WorldStructure stru);
    void add(String name, int x, int y, int z, NativeBlockSource region);
    void del(int x, int y, int z);
    WorldStructure getNearestStructure(Vector3 pos, int dimension, String name, boolean is);
}
