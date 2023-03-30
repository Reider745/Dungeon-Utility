package com.reider.dungeonutility.struct.generation;

import com.reider.dungeonutility.struct.generation.thread.Algorithms;
import com.reider.dungeonutility.struct.generation.thread.Generation;
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
    public static Generation generation = new Generation();

    public static void callbackGeneration(int X, int Z, Random random, int dimension){
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
                Generation.spawn(description, pos, region, object, random, dimension);
            else
                Generation.addGenStructure(new Generation.SpawnedStructure(description, pos, region, object, random, dimension));
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

        description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
        if(description.isPoolStructure(pos, random, dimension, region))
            structures.add(new WorldStructure(pos, description.getName(), dimension));
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
