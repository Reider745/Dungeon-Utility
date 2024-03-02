package com.reider.dungeonutility.struct.generation.util;

import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.IChunk;
import com.reider.dungeonutility.struct.generation.types.api.NativeChunk;

public class NativeChunkManager implements IChunkManager {
    private static native int[] nativeGetDimensions();
    private static native boolean nativeIsChunckLoaded(int dimension, int x, int z);
    private static native boolean nativeCanSpawn(int dimension, int sX, int sZ, int eX, int eZ);
    private static native long nativeRemove(int dimension);
    private static native void nativeAdd(long ptr);
    private static native int nativeGetCount();
    private static native int nativeGetCountByDimension(int dimension);
    private static native void nativeClear();
    private static native void nativeSetNotClear(int dimension, int sX, int sZ, int eX, int eZ);
    private static native long nativeAt(int dimension, int x, int z);

    @Override
    public int[] getDimensions() {
        synchronized(this){
            return nativeGetDimensions();
        }
    }

    public IChunk at(int dimension, int x, int z){
        long ptr = nativeAt(dimension, x, z);
        if(ptr == 0)
            return null;
        return new NativeChunk(ptr);
    }

    @Override
    public void add(IChunk chunk) {
        synchronized(this){
            if(!(chunk instanceof NativeChunk)){
                nativeAdd(new NativeChunk(chunk.getDimension(), chunk.getX(), chunk.getZ()).getPointer());
                chunk.free();
            }
            nativeAdd(((NativeChunk) chunk).getPointer());
        }
    }

    @Override
    public void add(int dimension, int x, int z) {
        synchronized(this){
            nativeAdd(new NativeChunk(dimension, x, z).getPointer());
        }
    }


    @Override
    public boolean isChunckLoaded(int dimension, int x, int z) {
        synchronized(this){
            return nativeIsChunckLoaded(dimension, x, z);
        }
    }

    @Override
    public void setNotClear(int dimension, int sX, int sZ, int eX, int eZ) {
        synchronized(this){
            nativeSetNotClear(dimension, sX, sZ, eX, eZ);
        }
    }

    @Override
    public boolean canSpawn(int dimension, int sX, int sZ, int eX, int eZ) {
        synchronized(this){
            return nativeCanSpawn(dimension, sX, sZ, eX, eZ);
        }
    }

    @Override
    public IChunk remove(int dimension) {
        synchronized(this){
            long ptr = nativeRemove(dimension);
            if(ptr == 0) return null;
            return new NativeChunk(ptr);
        }
    }

    @Override
    public int getCount() {
        synchronized(this){
            return nativeGetCount();
        }
    }

    @Override
    public int getCount(int dimension) {
        synchronized(this){
            return nativeGetCountByDimension(dimension);
        }
    }

    @Override
    public void clear() {
        synchronized(this){
            nativeClear();
        }
    }
}
