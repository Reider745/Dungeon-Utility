package com.reider.dungeonutility.struct.generation.types.api;

public class NativeChunk implements IChunk {
    private static native long createChunk(int dimension, int x, int z, long time);
    private static native int nativeGetDimension(long ptr);
    private static native int nativeGetX(long ptr);
    private static native int nativeGetZ(long ptr);
    private static native long nativeGetTime(long ptr);
    private static native boolean nativeCanClear(long ptr);
    private static native void nativeSetCanClear(long ptr, boolean value);
    private static native void nativeFree(long ptr);

    private long ptr;

    public NativeChunk(int dimension, int x, int z){
        ptr = createChunk(dimension, x, z, System.currentTimeMillis());
    }

    public NativeChunk(long ptr){
        this.ptr = ptr;
    }

    @Override
    public int getDimension() {
        return nativeGetDimension(ptr);
    }

    @Override
    public int getX() {
        return nativeGetX(ptr);
    }

    @Override
    public int getZ() {
        return nativeGetZ(ptr);
    }

    @Override
    public long getTime() {
        return nativeGetTime(ptr);
    }

    @Override
    public boolean canClear() {
        return nativeCanClear(ptr);
    }

    @Override
    public void setCanClear(boolean value) {
        nativeSetCanClear(ptr, value);
    }
    
    @Override
    public void free() {
        nativeFree(ptr);
    }

    public long getPointer(){
        return ptr;
    }
}