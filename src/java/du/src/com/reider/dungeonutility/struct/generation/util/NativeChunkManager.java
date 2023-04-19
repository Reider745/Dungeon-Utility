package com.reider.dungeonutility.struct.generation.util;

import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.IChunk;
import com.reider.dungeonutility.struct.generation.types.api.NativeChunk;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

public class NativeChunkManager implements IChunkManager {

    private static native int[] nativeGetDimensions();
    private static native boolean nativeIsChunckLoaded(int dimension, int x, int z);
    private static native long nativeRemove(int dimension);
    private static native void nativeAdd(long ptr);
    private static native int nativeGetCount();
    private static native int nativeGetCountByDimension(int dimension);
    private static native void nativeClear();

    @Override
    public int[] getDimensions() {
        return nativeGetDimensions();
    }

    @Override
    public void add(IChunk chunk) {
        if(!(chunk instanceof NativeChunk)){
            nativeAdd(new NativeChunk(chunk.getDimension(), chunk.getX(), chunk.getZ()).getPointer());
            chunk.free();
            Logger.error("Not chunk NativeChunk");
        }
        nativeAdd(((NativeChunk) chunk).getPointer());
    }

    @Override
    public void add(int dimension, int x, int z) {
        add(new NativeChunk(dimension, x, z));
    }


    @Override
    public boolean isChunckLoaded(int dimension, int x, int z) {
        return nativeIsChunckLoaded(dimension, x, z);
    }

    @Override
    public IChunk remove(int dimension) {
        return new NativeChunk(nativeRemove(dimension));
    }

    @Override
    public int getCount() {
        return nativeGetCount();
    }

    @Override
    public int getCount(int dimension) {
        return nativeGetCountByDimension(dimension);
    }

    @Override
    public void clear() {
        nativeClear();
    }
}
