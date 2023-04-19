package com.reider.dungeonutility.struct.generation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.Chunk;
import com.reider.dungeonutility.struct.generation.types.api.IChunk;

public class ChunkManager implements IChunkManager {
    public HashMap<Integer, ArrayList<IChunk>> dimensions = new HashMap<>();

    public ArrayList<IChunk> getChunks(int dimension){
        ArrayList<IChunk> result = dimensions.get(dimension);
        if(result == null){
            result = new ArrayList<>();
            dimensions.put(dimension, result);
        }
        return result;
    }

    @Override
    public void add(IChunk chunk) {
        synchronized(dimensions){
            getChunks(chunk.getDimension()).add(chunk);
        }
    }

    @Override
    public void add(int dimension, int x, int z) {
        synchronized(dimensions){
            getChunks(dimension).add(new Chunk(dimension, x, z));
        }
    }

    @Override
    public boolean isChunckLoaded(int dimension, int x, int z) {
        synchronized(dimensions){
            ArrayList<IChunk> chunks = getChunks(dimension);
            for(IChunk pos : chunks)
                if(pos.getX() == x && pos.getZ() == z)
                    return true;
        }
        return false;
    }

    @Override
    public IChunk remove(int dimension) {
        synchronized(dimensions){
            return getChunks(dimension).remove(0);
        }
    }

    @Override
    public int getCount() {
        synchronized(dimensions){
            int result = 0;
            Collection<ArrayList<IChunk>> array = dimensions.values();
            for(ArrayList<IChunk> list : array)
                result += list.size();
            return result;
        }
    }

    @Override
    public int[] getDimensions() {
        synchronized(dimensions){
            Set<Integer> set = dimensions.keySet();
            Iterator<Integer> it = set.iterator();
            int size = set.size();
            int[] result = new int[size];
            for(int i = 0;i < size;i++)
                result[i] = it.next();
            return result;
        }
    }

    @Override
    public int getCount(int dimension){
        synchronized(dimensions){
            return getChunks(dimension).size();
        }
    }

    @Override
    public void clear(){
        synchronized(this.dimensions){
            this.dimensions.clear();
        }
    }
}
