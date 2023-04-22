package com.reider.dungeonutility.struct.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.reider.Debug;
import com.reider.dungeonutility.struct.StructureUtility;
import com.reider.dungeonutility.struct.StructureUtility.Size;
import com.reider.dungeonutility.struct.generation.types.IGenerationDescription;
import com.reider.dungeonutility.struct.generation.types.IGenerationType;
import com.reider.dungeonutility.struct.generation.types.IStructurePiece;
import com.reider.dungeonutility.struct.generation.types.IStructureStorage;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.runtime.other.WorldGen.ChunkPos;

public class StructurePiece implements IStructurePiece {
    public static class SpawnedStructure {
        public Size[] size;
        public IGenerationDescription description;
        public Vector3 pos;
        public Object object;
        public Random random;
        public int dimension;
        public ChunkPos start;
        public ChunkPos end;
        public long time;
        public StructurePiece self;

        public SpawnedStructure(StructurePiece self, IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension){
            this.self = self;

            this.size = StructureUtility.getStructureSize(description.getStructure().getStructure());
            this.description = description;
            this.pos = pos;
            this.object = object;
            this.random = random;
            this.dimension = dimension;

            this.start = new ChunkPos(dimension, (int) Math.floor((pos.x + size[0].min) / 16), (int) Math.floor((pos.z + size[2].min) / 16));
            this.end = new ChunkPos(dimension, (int) Math.floor((pos.x + size[0].max) / 16), (int) Math.floor((pos.z + size[2].max) / 16));
            StructurePieceController.getChunkManager().setNotClear(dimension, start.x, start.z, end.x, end.z);

            this.time = System.currentTimeMillis();
        }

        public boolean canChunk(int X, int Z){
            return start.x >= X && X <= end.x && start.z >= Z && Z <= end.z;
        }

        public boolean canChunk(ChunkPos pos){
            return canChunk(pos.x, pos.z);
        }

        public boolean canSpawn(NativeBlockSource region){
            if(region.getDimension() != dimension) return false;
            return StructurePieceController.getChunkManager().canSpawn(dimension, start.x, start.z, end.x, end.z);
        }

        public void spawn(NativeBlockSource region){
            self.spawnStructure(description, pos, region, object, random, dimension);
        }
    }

    public HashMap<String, IGenerationType> types = new HashMap<>();
    public ArrayList<IGenerationDescription> structures = new ArrayList<>();
    public ArrayList<SpawnedStructure> spawnedStructures = new ArrayList<>();

    @Override
    public void addGeneration(IGenerationDescription stru) {
        structures.add(stru);
    }

    @Override
    public void registerType(IGenerationType type) {
        types.put(type.getType(), type);
    }

    @Override
    public void generation(int x, int z, Random random, int dimension) {
        long start = System.currentTimeMillis();
        StructurePieceController.getChunkManager().add(dimension, x, z);
        NativeBlockSource region = NativeBlockSource.getCurrentWorldGenRegion();
        Scriptable object =  new ScriptableObject() {
            @Override
            public String getClassName() {
                return "object";
            }
        };

        object.put("random", object, random);
        for(IGenerationDescription stru : structures)
            generationStructure(stru, x, z, random, region, object);

        synchronized(spawnedStructures){
            ArrayList<SpawnedStructure> newList = new ArrayList<>();

            for(SpawnedStructure stru : spawnedStructures)
                if(stru.canSpawn(region))
                    stru.spawn(region);
                else if(!stru.description.canClearStructure() || System.currentTimeMillis() - stru.time <= stru.description.getTimeClearToMembory())
                    newList.add(stru);
            int size = spawnedStructures.size();
            spawnedStructures.clear();
            for(SpawnedStructure stru : newList)
                spawnedStructures.add(stru);
            if(size != spawnedStructures.size())
                Debug.get().updateСhart("structures_queue", "Structures queue", spawnedStructures.size());
        }
        Debug.get().updateСhart("generation", "Generation time ", (int) (System.currentTimeMillis() - start));
    }

    public void generationStructure(IGenerationDescription description, int x, int z,Random random, NativeBlockSource region, Scriptable packet){
        int dimension = region.getDimension();
        IGenerationType type = types.get(description.getType());
        if(type == null) return;
        int[] counts = description.getCount();
        int count = counts[random.nextInt(counts.length)];
        for(int i = 0;i < count;i++)
            if(random.nextInt(description.getChance()) <= 1) {
                Vector3 pos = type.getPosition(x, z, random, dimension, region);
                Vector3 offset = description.getOffset();
                int[] y = description.getMinAndMaxY();
                if(y[0] >= pos.y && pos.y <= y[1]){
                    boolean res = true;
                    for(int yy = y[0];yy < y[1];yy++)
                        if(region.getBlockId((int) pos.x, yy, (int) pos.z) == 0 && region.getBlockId((int) pos.x, yy-1, (int) pos.z) != 0){
                            pos = new Vector3(pos.x, yy, pos.z);
                            res = false;
                            break;
                        }
                    if(res)
                        return;
                }
                
                double distant = description.getDistance();
                if(distant != 0) {
                    WorldStructure nearest = StructurePieceController.getStorage().getNearestStructure(pos,dimension, description.getName(), description.checkName());
                    if (nearest != null && nearest.pos.distance(pos) < distant)
                        return;
                }
                if(!(type.isGeneration(pos, random, dimension, region) && description.isGeneration(pos, random, dimension, region)) || (description.isSet() && !description.getStructure().isSetStructure((int)pos.x, (int)pos.y, (int)pos.z, region)))
                    return;
                
                pos = new Vector3(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

                IStructureStorage storage = StructurePieceController.getStorage();
                if(description.isPoolStructure(pos, random, dimension, region))
                    storage.add(new WorldStructure(pos, description.getName(), dimension));
                    
                if(description.canLegacySpawn())
                    spawnStructure(description, pos, region, packet, random, dimension);
                else{
                    SpawnedStructure stru = new SpawnedStructure(this, description, pos, region, packet, random, dimension);
                    if(stru.canSpawn(region))
                        stru.spawn(region);
                    else
                        addGenStructure(stru);
                }
            }
    }

    public void addGenStructure(SpawnedStructure stru){
        synchronized(spawnedStructures){
            spawnedStructures.add(stru);
            Debug.get().updateСhart("structures_queue", "Structures queue", spawnedStructures.size());
        }
    }

    @Override
    public void spawnStructure(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension) {
        description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
        if(description.canOptimization())
            StructurePieceController.algorithms.addPos(pos);
    }
}