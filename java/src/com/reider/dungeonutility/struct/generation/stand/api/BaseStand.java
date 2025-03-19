package com.reider.dungeonutility.struct.generation.stand.api;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

import java.util.function.Function;

public abstract class BaseStand {
    private String name;
    private StructureUtility.Size[] sizes;
    private BlockData[][] minimalBlocks;

    public int xOffset, zOffset;
    public int lengthX, lengthZ;
    protected int startBuildStand;

    protected BaseStand(Structure structure) {
        this.preProcess(structure.getStructure());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected abstract boolean isFindLowBlock();

    protected void preProcess(StructureDescription structure) {
        sizes = StructureUtility.getStructureSize(structure);

        lengthX = Math.abs(sizes[0].min) + Math.abs(sizes[0].max) + 1;
        lengthZ = Math.abs(sizes[2].min) + Math.abs(sizes[2].max) + 1;
        xOffset = Math.abs(sizes[0].min);
        zOffset = Math.abs(sizes[0].min);
        startBuildStand = sizes[1].min;

        minimalBlocks = new BlockData[lengthX][lengthZ];

        if(isFindLowBlock()) {
            for (BlockData block : structure.blocks) {
                Logger.debug("TEST", block.y + " " + startBuildStand);
                if (block.y == startBuildStand && block.getData().state.id != 0)
                    minimalBlocks[block.x + xOffset][block.z + zOffset] = block;
            }
        }
    }

    protected boolean isReplaceBlock(int id) {
        return id == 0 || (id >= 8 && id <= 11);
    }

    protected void forEach(int x, int y, int z, NativeBlockSource region, Function<Integer, Boolean> func) {
        for (int offset = 1; isReplaceBlock(region.getBlockId(x, y - offset, z)) && y - offset >= 0 && func.apply(offset); offset++) {
        }
    }

    protected void placeBlockProcess(NativeBlockSource region, int x, int y, int z, BlockData block) {}

    protected void placeBlockProcess(NativeBlockSource region, int x, int y, int z, BlockData block, int sx, int sz) {
        placeBlockProcess(region, x, y, z, block);
    }

    public void setStand(NativeBlockSource region, int x, int y, int z) {
        for(int sx = this.sizes[0].min;sx <= this.sizes[0].max;sx++) {
            for(int sz = this.sizes[2].min;sz <= this.sizes[2].max;sz++) {
                placeBlockProcess(region, x, y, z, minimalBlocks[sx + xOffset][sz + zOffset], sx, sz);
            }
        }
    }

    public void setStandPart(NativeBlockSource region, int x, int y, int z, int startIndexX, int endX, int startIndexZ, int endZ) {
        for(int sx = startIndexX;sx <= endX;sx++) {
            for(int sz = startIndexZ;sz <= endZ;sz++) {
                placeBlockProcess(region, x, y, z, minimalBlocks[sx][sz]);
            }
        }
    }
}
