package com.reider.dungeonutility.struct.generation.types.api;

public class Chunk implements IChunk {
    private int dimension, x, z;
    private long time;

    public Chunk(int dimension, int x, int z){
        this.dimension = dimension;
        this.x = x;
        this.z = z;

        time = System.currentTimeMillis();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public boolean canClear() {
        return false;
    }

    @Override
    public void setCanClear(boolean value) {
        
    }

    @Override
    public void free() {
        
    }
}
