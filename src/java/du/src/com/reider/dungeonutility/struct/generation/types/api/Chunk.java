package com.reider.dungeonutility.struct.generation.types.api;

import com.zhekasmirnov.innercore.api.runtime.other.WorldGen.ChunkPos;

public class Chunk extends ChunkPos {
    public long time;
    public Chunk(int dimension, int x, int z){
        super(dimension, x, z);
        time = System.currentTimeMillis();
    }
}
