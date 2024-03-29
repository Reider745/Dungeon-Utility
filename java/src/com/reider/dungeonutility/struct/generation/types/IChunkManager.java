package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.struct.generation.types.api.IChunk;

public interface IChunkManager {
    int[] getDimensions();
    void add(IChunk chunk);
    void add(int dimension, int x, int z);
    boolean isChunckLoaded(int dimension, int x, int z);
    IChunk remove(int dimension);
    IChunk at(int dimension, int x, int z);
    boolean canSpawn(int dimension, int sX, int sZ, int eX, int eZ);
    void setNotClear(int dimension, int sX, int sZ, int eX, int eZ);
    int getCount();
    int getCount(int dimension);
    void clear();
}
