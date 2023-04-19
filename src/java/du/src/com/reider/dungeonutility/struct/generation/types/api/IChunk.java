package com.reider.dungeonutility.struct.generation.types.api;

public interface IChunk {
    int getDimension();
    int getX();
    int getZ();
    long getTime();
    boolean canClear();
    void setCanClear(boolean value);
    void free();
}