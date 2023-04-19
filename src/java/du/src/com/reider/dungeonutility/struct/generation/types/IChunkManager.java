package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.struct.generation.types.api.IChunk;

public interface IChunkManager {
    int[] getDimensions();
    void add(IChunk chunk);
    void add(int dimension, int x, int z);
    boolean isChunckLoaded(int dimension, int x, int z);
    IChunk remove(int dimension);
    int getCount();
    int getCount(int dimension);
    void clear();
}
