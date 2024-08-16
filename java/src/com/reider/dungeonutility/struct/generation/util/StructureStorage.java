package com.reider.dungeonutility.struct.generation.util;

import java.util.ArrayList;

import com.reider.dungeonutility.struct.generation.types.IStructureStorage;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class StructureStorage implements IStructureStorage {
    private final ArrayList<WorldStructure> structures = new ArrayList<>();

    @Override
    public void add(String name, int x, int y, int z, NativeBlockSource region) {
        synchronized(structures){
            structures.add(new WorldStructure(new Vector3(x, y, z), name, region.getDimension()));
        }
    }

    @Override
    public void del(int x, int y, int z) {
        synchronized(structures){
            for(int i = 0;i < structures.size();i++) {
                WorldStructure structure = structures.get(i);
                if (structure.pos.x == x && structure.pos.y == y && structure.pos.z == z) {
                    structures.remove(i);
                    return;
                }
            }
        }
    }

    @Override
    public WorldStructure getNearestStructure(Vector3 pos, int dimension, String name, boolean is) {
        synchronized(structures){
            WorldStructure result = null;
            for (WorldStructure structure : structures) {
                if (structure == null || structure.dimension != dimension && (is || !structure.name.equals(name)))
                    continue;

                if (result == null) {
                    result = structure;
                    continue;
                }

                if (pos.distance(result.pos) >= pos.distance(structure.pos)) {
                    result = structure;
                }
            }
            return result;
        }
    }

    @Override
    public void add(WorldStructure stru) {
        if(stru == null) return;
        synchronized(structures){
            structures.add(stru);
        }
    }

    private static final WorldStructure[] EMPTY_WORLD_STRUCTURE = new WorldStructure[0];

    @Override
    public WorldStructure[] getStructures() {
        synchronized(structures){;
            return structures.toArray(EMPTY_WORLD_STRUCTURE);
        }
    }

    @Override
    public void setStructures(WorldStructure[] structures) {
        synchronized(this.structures){
            this.structures.clear();
            for(WorldStructure stru : structures)
                this.add(stru);
        }
    }

    @Override
    public void clear(){
        synchronized(structures){
            this.structures.clear();
        }
    }
}
