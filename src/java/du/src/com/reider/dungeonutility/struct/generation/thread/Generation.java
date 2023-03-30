package com.reider.dungeonutility.struct.generation.thread;

import java.util.ArrayList;
import java.util.Random;

import com.reider.dungeonutility.struct.StructureUtility;
import com.reider.dungeonutility.struct.StructureUtility.Size;
import com.reider.dungeonutility.struct.generation.IGenerationDescription;
import com.reider.dungeonutility.struct.generation.StructurePiece;
import com.reider.dungeonutility.struct.generation.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI.BlockSource;
import com.zhekasmirnov.innercore.api.runtime.other.WorldGen.ChunkPos;

public class Generation extends Thread {
    private static long time = 1000l;

    public static class SpawnedStructure {
        public Size[] size;
        public IGenerationDescription description;
        public Vector3 pos;
        public NativeBlockSource region;
        public Object object;
        public Random random;
        public int dimension;
        public ChunkPos start;
        public ChunkPos end;

        public SpawnedStructure(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension){
            this.size = StructureUtility.getStructureSize(description.getStructure().getStructure());
            this.description = description;
            this.pos = pos;
            this.region = BlockSource.getDefaultForDimension(dimension);
            this.object = object;
            this.random = random;
            this.dimension = dimension;

            this.start = new ChunkPos(dimension, (int) Math.floor(size[0].min / 16), (int) Math.floor(size[2].min / 16));
            this.end = new ChunkPos(dimension, (int) Math.floor(size[0].max / 16), (int) Math.floor(size[2].max / 16));
        }

        public boolean canSpawn(){
            for(int X = start.x;X <= end.x;X++)
                for(int Z = start.z;Z <= end.z;Z++)
                    if(!region.isChunkLoaded(X, Z))
                        return false;
            return true;
        }

        public void spawn(){
            Generation.spawn(description, pos, region, object, random, dimension);
        }
    }
    private static ArrayList<SpawnedStructure> list = new ArrayList<>();

    public Generation(){
        this.start();
    }


    @Override
    public void run() {
        while(true){
            try {
                long start = System.currentTimeMillis();
                synchronized(list){
                    int size = list.size();
                    for(SpawnedStructure stru : list){
                        if(stru.canSpawn())
                            stru.spawn();
                    }
                    NativeAPI.clientMessage(""+(System.currentTimeMillis()-start)+" "+list.size());
                    list.clear();
                }
                this.sleep(time);
            } catch (Exception e) {
                Logger.error(e.getLocalizedMessage());
                NativeAPI.clientMessage(e.getLocalizedMessage());
            }
        }
    }

    public static void spawn(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension){
        description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
        if(description.isPoolStructure(pos, random, dimension, region)) {
            StructurePiece.structure_spawn++;
            StructurePiece.structures.add(new WorldStructure(pos, description.getName(), dimension));
        }
        if(description.canOptimization())
            Algorithms.addPos(pos);
    }

    public static void addGenStructure(SpawnedStructure stru) {
        synchronized(list){
            list.add(stru);
        }
    }
}
