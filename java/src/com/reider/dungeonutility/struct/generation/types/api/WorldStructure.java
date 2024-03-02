package com.reider.dungeonutility.struct.generation.types.api;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public class WorldStructure {
    public int dimension;
    public Vector3 pos;
    public String name;

    public WorldStructure(Vector3 pos, String name, int dimension) {
        this.pos = pos;
        this.name = name;
        this.dimension = dimension;
    }
}