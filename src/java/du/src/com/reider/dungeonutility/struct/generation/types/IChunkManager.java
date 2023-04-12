package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.struct.generation.types.api.Chunk;

public interface IChunkManager {
    int[] getDimensions();
    void add(Chunk chunk);
    void add(int dimension, int x, int z);
    boolean isChunckLoaded(int dimension, int x, int z);
    Chunk remove(int dimension);
    int getCount();
    int getCount(int dimension);
    void clear();
}
