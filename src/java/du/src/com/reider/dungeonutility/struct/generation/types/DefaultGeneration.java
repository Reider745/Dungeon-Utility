package com.reider.dungeonutility.struct.generation.types;

import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.IGenerationDescription;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public class DefaultGeneration implements IGenerationDescription {
    public String type;
    public String name;
    public int chance;
    public Structure structure;
    public int disnatnt;
    public boolean pool;
    public boolean isSet;
    public int x;
    public int y;
    public int z;
    public int dimension;
    public boolean white_list;
    public int[] biomes;
    public boolean checkName;
    public boolean white_list_blocks;
    public int[] blocks;
    public boolean optimization;
    public boolean legacy;
    public long time;

    public DefaultGeneration(String type, String name, int x, int y, int z, int chance, int disnatnt, boolean pool, boolean isSet, int dimension, boolean white_list, int[] biomes, boolean white_list_blocks, int[] blocks, Structure structure, boolean checkName, boolean optimization, boolean legacy, long time){
        this.type = type;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chance = chance;
        this.disnatnt = disnatnt;
        this.pool = pool;
        this.isSet = isSet;
        this.dimension = dimension;
        this.white_list = white_list;
        this.biomes = biomes;
        this.structure = structure;
        this.white_list_blocks = white_list_blocks;
        this.blocks = blocks;
        this.checkName = checkName;
        this.optimization = optimization;
        this.legacy = legacy;
        this.time = time;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getChance() {
        return chance;
    }

    @Override
    public Structure getStructure() {
        return structure;
    }

    private int indexOf(int x, int[] list){
        for(int i = 0;i < list.length;i++) {
            if (list[i] == x)
                return i;
        }
        return -1;
    }

    @Override
    public boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region) {
        int biome = region.getBiome((int)pos.x, (int)pos.z);
        int id = region.getBlockId((int)pos.x, (int)pos.y, (int) pos.z);
        return this.dimension == dimension && (white_list ? indexOf(biome, biomes) != -1 : indexOf(biome, biomes) == -1) && (white_list_blocks ? indexOf(id, blocks) != -1 : indexOf(id, blocks) == -1);
    }

    @Override
    public double getDistance() {
        return disnatnt;
    }

    @Override
    public boolean isPoolStructure(Vector3 pos, Random random, int dimension, NativeBlockSource region) {
        return pool;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public Vector3 getOffset() {
        return new Vector3(x, y, z);
    }

    @Override
    public boolean checkName() {
        return checkName;
    }

    @Override
    public boolean canOptimization() {
        return optimization;
    }

    @Override
    public boolean canLegacySpawn() {
        return legacy;
    }

    @Override
    public long getTimeClearToMembory() {
        return time;
    }
}
