package com.reider.dungeonutility.struct.generation.types.api;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.types.IGenerationDescription;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public class DefaultGeneration implements IGenerationDescription {
    public String type, name, identifier;
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
    public int[] count;
    public int[] minAndMaxY;
    public boolean _canLegacyOffset;
    public String standName;

    public DefaultGeneration(String type, String name, int x, int y, int z, int chance, int disnatnt, boolean pool, boolean isSet, int dimension, boolean white_list, int[] biomes, boolean white_list_blocks, int[] blocks, Structure structure, boolean checkName, boolean optimization, boolean legacy, long time, int[] count, int[] minAndMaxY, boolean canLegacyOffset, String identifier, String standName){
        this.type = type;
        this.name = name;
        this.identifier = identifier;
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
        this.minAndMaxY = minAndMaxY;
        this.count = count;
        this._canLegacyOffset = canLegacyOffset;
        this.standName = standName;
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

    @Override
    public void setStructure(StructureDescription description) {
        this.structure.setStructure(description);
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

    @Override
    public int[] getCount() {
        return this.count;
    }

    @Override
    public int[] getMinAndMaxY() {
        return this.minAndMaxY;
    }

    @Override
    public boolean canClearStructure() {
        return time != -1;
    }

    @Override
    public boolean canLegacyOffset() {
        return _canLegacyOffset;
    }

    @Override
    public String getUniqueIdentifier() {
        return identifier;
    }

    @Override
    public String getStandName() {
        return standName;
    }
}
