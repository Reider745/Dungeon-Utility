package com.reider.dungeonutility.struct.generation.util;

import java.util.ArrayList;

import com.reider.dungeonutility.struct.generation.types.IStructureStorage;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class StructureStorage implements IStructureStorage {
    public ArrayList<WorldStructure> structures = new ArrayList<>();

    @Override
    public void add(String name, int x, int y, int z, NativeBlockSource region) {
        structures.add(new WorldStructure(new Vector3(x, y, z), name, region.getDimension()));
    }

    @Override
    public void del(int x, int y, int z) {
        for(int i = 0;i < structures.size();i++) {
            WorldStructure structure = structures.get(i);
            if (structure.pos.x == x && structure.pos.y == y && structure.pos.z == z) {
                structures.remove(i);
                return;
            }
        }
    }

    @Override
    public WorldStructure getNearestStructure(Vector3 pos, int dimension, String name, boolean is) {
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

    @Override
    public void add(WorldStructure stru) {
        structures.add(stru);
    }

    @Override
    public WorldStructure[] getStructures() {
        Object[] array = structures.toArray();
        WorldStructure[] result = new WorldStructure[array.length];
        for(int i = 0;i < array.length;i++)
            result[i] = (WorldStructure) array[i];
        return result;
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
        synchronized(this.structures){
            this.structures.clear();
        }
    }
}
