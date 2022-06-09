package com.reider.dungeonutility.struct.generation;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public class WorldStructure {
    public WorldStructure(Vector3 pos, String name, int dimension) {
        this.pos = pos;
        this.name = name;
        this.dimension = dimension;
    }

    int dimension;
    public Vector3 pos;
    public String name;
}

