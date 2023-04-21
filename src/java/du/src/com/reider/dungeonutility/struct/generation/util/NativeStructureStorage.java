package com.reider.dungeonutility.struct.generation.util;

import com.reider.dungeonutility.struct.generation.types.IStructureStorage;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class NativeStructureStorage implements IStructureStorage {

    @Override
    public WorldStructure[] getStructures() {
        return null;
    }

    @Override
    public void setStructures(WorldStructure[] structure) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void add(WorldStructure stru) {

    }

    @Override
    public void add(String name, int x, int y, int z, NativeBlockSource region) {

    }

    @Override
    public void del(int x, int y, int z) {

    }

    @Override
    public WorldStructure getNearestStructure(Vector3 pos, int dimension, String name, boolean is) {
        return null;
    }
}
