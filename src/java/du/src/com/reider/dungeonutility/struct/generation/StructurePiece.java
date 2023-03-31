package com.reider.dungeonutility.struct.generation;

import com.reider.dungeonutility.struct.generation.thread.Algorithms;
import com.reider.dungeonutility.struct.generation.thread.ChunckClearMembory;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.StructureUtility;
import com.reider.dungeonutility.struct.StructureUtility.Size;
import com.zhekasmirnov.innercore.api.NativeAPI;
import com.zhekasmirnov.innercore.api.runtime.other.WorldGen.ChunkPos;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StructurePiece {
    public static HashMap<String, IGenerationType> types = new HashMap<>();
    public static ArrayList<IGenerationDescription> descriptions = new ArrayList<>();
    public static ArrayList<WorldStructure> structures = new ArrayList<>();

    public static long structure_spawn = 0;

    public static boolean critical_release = false;
    public static int critical_release_activation = 500;
    public static int critical_release_radius = 1000;

    public static boolean clearing_clusters = false;
    public static int clearing_clusters_activation = 50;
    public static int clearing_clusters_radius = 20;

    public static void setCriticalReleaseSetting(boolean enable, int activation, int radius){
        critical_release = enable;
        critical_release_activation = activation;
        critical_release_radius = radius;
    }

    public static void setClearingClustersSetting(boolean enable, int activation, int radius){
        clearing_clusters = enable;
        clearing_clusters_activation = activation;
        clearing_clusters_radius = radius;
    }

    public static void algorithmsOptimization(Vector3 pos){
        if(critical_release){
            int size = structures.size();
            if(size > critical_release_activation)
                for(int i = 0;i < size;i++){
                    Vector3 pos_structure = structures.get(i).pos;
                    if(pos.distance(pos_structure) > critical_release_radius)
                        structures.set(i, null);
                }
        }

        if(clearing_clusters && structure_spawn % clearing_clusters_activation == 0){
            int size = structures.size();
            for(int i = 0;i < size;i++){
                WorldStructure gl = structures.get(i);
                if(gl==null)
                    continue;
                for(int j = 0;j < size;j++){
                    WorldStructure structure = structures.get(j);
                    if(structure!=null&&structure.pos.distance(gl.pos) <= clearing_clusters_radius)
                        structures.set(j, null);
                }
            }
        }

        ArrayList<WorldStructure> newList = new ArrayList<>();
        for(int i = 0;i < structures.size();i++){
            WorldStructure structure = structures.get(i);
            if(structure != null)
                newList.add(structure);
        }

        structures = newList;
    }

    public static Algorithms algorithms = new Algorithms();
    public static ChunckClearMembory generation = new ChunckClearMembory();

    public static void callbackGeneration(int X, int Z, Random random, int dimension){
        loaded.add(new ChunckPosTime(dimension, X, Z));
        NativeBlockSource region = NativeBlockSource.getCurrentWorldGenRegion();
        Scriptable object =  new ScriptableObject() {
            @Override
            public String getClassName() {
                return "object";
            }
        };
        object.put("random", object, random);
        for(int i = 0;i < descriptions.size();i++)
            generateStructure(descriptions.get(i), X, Z, random, region, object);
        synchronized(list_structures){
            ArrayList<SpawnedStructure> newList = new ArrayList<>();

            for(int i = 0;i < list_structures.size();i++){
                SpawnedStructure stru = list_structures.get(i);
                if(stru.canSpawn()){
                    stru.spawn(region);
                    list_structures.set(i, null);
                }else if(System.currentTimeMillis() - stru.time <= stru.description.getTimeClearToMembory())
                    newList.add(stru);
            }

            list_structures = newList;
        }
    }

    public static ArrayList<ChunckPosTime> loaded = new ArrayList<>();

    public static class ChunckPosTime extends ChunkPos {
        public long time;

        public ChunckPosTime(int dimension, int x, int y){
            super(dimension, x, y);
            this.time = System.currentTimeMillis();
        }
    }

    public static boolean isChunckLoaded(int dimension, int X, int Z){
        ChunkPos pos_ = new ChunkPos(dimension, X, Z);
        synchronized(loaded){
            for(ChunkPos pos : loaded)
                if(pos.equals(pos_))
                    return true;
        }
        return false;
    }

    public static class SpawnedStructure {
        public Size[] size;
        public IGenerationDescription description;
        public Vector3 pos;
        public Object object;
        public Random random;
        public int dimension;
        public ChunkPos start;
        public ChunkPos end;
        public boolean v;
        public long time;

        public SpawnedStructure(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension, boolean v){
            this.size = StructureUtility.getStructureSize(description.getStructure().getStructure());
            this.description = description;
            this.pos = pos;
            this.object = object;
            this.random = random;
            this.dimension = dimension;
            this.v = v;

            this.start = new ChunkPos(dimension, (int) Math.floor(size[0].min / 16), (int) Math.floor(size[2].min / 16));
            this.end = new ChunkPos(dimension, (int) Math.floor(size[0].max / 16), (int) Math.floor(size[2].max / 16));

            this.time = System.currentTimeMillis();
        }

        public boolean canChunk(int X, int Z){
            return start.x >= X && X <= end.x && start.z >= Z && Z <= end.z;
        }

        public boolean canChunk(ChunkPos pos){
            return canChunk(pos.x, pos.z);
        }

        public boolean canSpawn(){
            for(int X = start.x;X <= end.x;X++)
                for(int Z = start.z;Z <= end.z;Z++)
                    if(!StructurePiece.isChunckLoaded(dimension, X, Z))
                        return false;
            return true;
        }

        public void spawn(NativeBlockSource region){
            StructurePiece.spawn(description, pos, region, object, random, dimension, v);
        }
    }

    public static void spawn(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension, boolean v){
        if(v){
            description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
            if(description.isPoolStructure(pos, random, dimension, region))
                structures.add(new WorldStructure(pos, description.getName(), dimension));
            return;
        }
        description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
        if(description.isPoolStructure(pos, random, dimension, region)) {
            StructurePiece.structure_spawn++;
            StructurePiece.structures.add(new WorldStructure(pos, description.getName(), dimension));
        }
        if(description.canOptimization())
            Algorithms.addPos(pos);
    }

    public static ArrayList<SpawnedStructure> list_structures = new ArrayList<>();

    public static void addGenStructure(SpawnedStructure stru) {
        synchronized(list_structures){
            list_structures.add(stru);
        }
    }

    public static void generateStructure(IGenerationDescription description, int X, int Z, Random random, NativeBlockSource region, Scriptable object){
        int dimension = region.getDimension();
        IGenerationType type = types.get(description.getType());
        if(type != null && random.nextInt(description.getChance()) <= 1) {
            Vector3 pos = type.getPosition(X, Z, random, dimension, region);
            Vector3 offset = description.getOffset();
            pos = new Vector3(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
            double distant = description.getDistance();
            if(distant != 0) {
                WorldStructure nearest = getNearestStructure(pos,dimension, description.getName(), description.checkName());
                if (nearest != null && nearest.pos.distance(pos) < distant)
                    return;
            }
            if(!(type.isGeneration(pos, random, dimension, region) && description.isGeneration(pos, random, dimension, region)) || (description.isSet() && !description.getStructure().isSetStructure((int)pos.x, (int)pos.y, (int)pos.z, region)))
                return;
            
            if(description.canLegacySpawn())
                StructurePiece.spawn(description, pos, region, object, random, dimension, false);
            else
                addGenStructure(new SpawnedStructure(description, pos, region, object, random, dimension, false));
        }
    }

    public static void generateStructure(IGenerationDescription description, int x, int y, int z, Random random, NativeBlockSource region, Scriptable object){
        int dimension = region.getDimension();
        Vector3 pos = new Vector3(x, y, z);
        Vector3 offset = description.getOffset();
        IGenerationType type = types.get(description.getType());
        pos = new Vector3(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
        double distant = description.getDistance();
        if(distant != 0) {
            WorldStructure nearest = getNearestStructure(pos,dimension, description.getName(), description.checkName());
            if (nearest != null && nearest.pos.distance(pos) < distant)
                return;
        }
        if(!(type.isGeneration(pos, random, dimension, region) && description.isGeneration(pos, random, dimension, region)) || (description.isSet() && !description.getStructure().isSetStructure((int)pos.x, (int)pos.y, (int)pos.z, region)))
            return;

        if(description.canLegacySpawn())
            StructurePiece.spawn(description, pos, region, object, random, dimension, true);
        else
            addGenStructure(new SpawnedStructure(description, pos, region, object, random, dimension, true));
    }

    public static WorldStructure getNearestStructure(Vector3 pos, int dimension, String name, boolean is){
        WorldStructure result = null;
        for(int i = 0;i < structures.size();i++){
            WorldStructure structure = structures.get(i);
            if(structure.dimension != dimension && (is || !structure.name.equals(name)))
                continue;
            if(result == null){
                result = structure;
                continue;
            }

            if(pos.distance(result.pos) >= pos.distance(structure.pos)){
                result = structure;
                continue;
            }
        }
        return result;
    }

    public static void add(String name, int x, int y, int z, NativeBlockSource region){
        structures.add(new WorldStructure(new Vector3(x, y, z), name, region.getDimension()));
    }
    public static void del(int x, int y, int z){
        for(int i = 0;i < structures.size();i++) {
            WorldStructure structure = structures.get(i);
            if (structure.pos.x == x && structure.pos.y == y && structure.pos.z == z) {
                structures.remove(i);
                return;
            }
        }
    }

    public static void registerType(IGenerationType type){
        types.put(type.getType(), type);
    }

    public static void register(IGenerationDescription description){
        descriptions.add(description);
    }
}
