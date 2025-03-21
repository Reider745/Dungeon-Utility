package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.struct.loaders.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.loaders.StructurePool;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeTileEntity;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StructureUtility {
    public static BlockData[] getBlocksByArrayList(ArrayList<BlockData> blocks){
        final BlockData[] result = new BlockData[blocks.size()];
        for(int i = 0;i < blocks.size();i++)
            result[i] = blocks.get(i);
        return result;
    }

    public static ArrayList<BlockData> getArrayListByBlocks(BlockData[] blocks){
        return new ArrayList<>(Arrays.asList(blocks));
    }

    private interface IRotateBlock {
        BlockData onRotate(BlockData original);
    }

    private static final IRotateBlock[] ROTATES = new IRotateBlock[]{
            (data -> new BlockData(data.x, data.y, data.z,data)),
            (data -> new BlockData(-data.x, data.y, data.z,data)),
            (data -> new BlockData(-data.x, data.y, -data.z,data)),
            (data -> new BlockData(data.x, data.y, -data.z,data)),
            (data -> new BlockData(data.x, -data.y, data.z,data)),
            (data -> new BlockData(-data.x, -data.y, data.z,data)),
            (data -> new BlockData(-data.x, -data.y, -data.z,data)),
            (data -> new BlockData(data.x, -data.y, -data.z,data))
    };

    private static final IRotateBlock DEFAULT_ROTATE = (data -> new BlockData(data.x, data.y, data.z,data));

    public static StructureDescription rotate(StructureDescription stru, int rotate){
        final BlockData[] result = new BlockData[stru.blocks.length];
        final IRotateBlock rotateFunc;

        if(rotate < ROTATES.length)
            rotateFunc = ROTATES[rotate];
        else
            rotateFunc = DEFAULT_ROTATE;

        for(int i = 0;i < stru.blocks.length;i++)
            result[i] = rotateFunc.onRotate(stru.blocks[i]);

        return new StructureDescription(result);
    }

    public static StructureDescription rotate(StructureDescription stru, StructureRotation rotate){
        return rotate(stru, rotate.ordinal());
    }

    public static void registerRotations(StructurePool savePool, StructureDescription stru, String name, int[] rotates){
        for(int i : rotates) {
            savePool.setStructure(name + "_" + i, rotate(stru, i));
            AdaptedScriptAPI.Logger.Log("register rotate - " + name + "_" + i, "Dungeon Utility");
        }
    }

    public static void registerRotations(StructureDescription stru, String name, int[] rotates){
        registerRotations(StructureLoader.default_pool, stru, name, rotates);
    }

    public static void registerRotations(StructurePool savePool, StructureDescription stru, String name, StructureRotation[] rotates) {
        for (StructureRotation i : rotates) {
            savePool.setStructure(name + "_" + i, rotate(stru, i));
            AdaptedScriptAPI.Logger.Log("register rotate - " + name + "_" + i.name(), "Dungeon Utility");
        }
    }

    public static void registerRotations(StructureDescription stru, String name, StructureRotation[] rotates) {
        registerRotations(StructureLoader.default_pool, stru, name, rotates);
    }

    @Deprecated
    public static void newStructure(String name){
        StructureLoader.loadRuntime(name, new StructureDescription(new BlockData[] {}));
    }

    public static int getCountBlock(StructureDescription stru){
        return stru.blocks.length;
    }

    @Deprecated
    public static String[] getAllStructureName(){
        return StructureLoader.getAllStructureName();
    }

    @Deprecated
    public static void copy(StructurePool pool, String name1, String name2, IStructureCopy copy) {
        pool.copy(name1, name2, copy);
    }

    @Deprecated
    public static void copy(String name1, String name2, IStructureCopy c){
        copy(StructureLoader.default_pool, name1, name2, c);
    }

    public static int getBlockIndex(StructureDescription stru, int x, int y, int z) {
        for (int i = 0; i < stru.blocks.length; i++) {
            BlockData block = stru.blocks[i];
            if (block.x == x && block.y == y && block.z == z)
                return i;
        }
        return -1;
    }

    public static BlockData getBlock(StructureDescription stru, int x, int y, int z){
        int index = getBlockIndex(stru, x, y, z);
        if(index != -1)
            return stru.blocks[index];
        return null;
    }

    public static StructureDescription addBlock(StructureDescription stru, BlockData data){
        ArrayList<BlockData> blocks = getArrayListByBlocks(stru.blocks);
        blocks.add(data);
        stru.blocks = getBlocksByArrayList(blocks);
        return stru;
    }

    public static StructureDescription setBlock(StructureDescription stru, BlockData data){
        ArrayList<BlockData> blocks = getArrayListByBlocks(stru.blocks);
        int index = getBlockIndex(stru, data.x, data.y, data.z);
        if(index == -1)
                return addBlock(stru, data);
        blocks.add(index, data);
        stru.blocks = getBlocksByArrayList(blocks);
        return stru;
    }

    public static StructureDescription setBlock(StructureDescription stru, int index, BlockData data){
        ArrayList<BlockData> blocks = getArrayListByBlocks(stru.blocks);
        if(index == -1)
            return addBlock(stru, data);
        blocks.add(index, data);
        stru.blocks = getBlocksByArrayList(blocks);
        return stru;
    }

    public static class Size {
        public int min;
        public int max;

        public Size(int min, int max){
            this.min = min;
            this.max = max;
        }

        public Size() {
            this.min = Integer.MAX_VALUE;
            this.max = Integer.MIN_VALUE;
        }

        @Override
        public String toString() {
            return "{max:"+max+",min:"+min+"}";
        }
    }

    public static Size[] getStructureSize(StructureDescription structure){
        final BlockData[] stru = structure.blocks;

        if(stru.length == 0)
            return new Size[] {
                    new Size(0, 0),
                    new Size(0, 0),
                    new Size(0, 0)
            };

        final Size[] size = {new Size(),new Size(),new Size()};
        for (BlockData block : stru) {
            size[0].max = Math.max(size[0].max, block.x);
            size[0].min = Math.min(size[0].min, block.x);

            size[1].max = Math.max(size[1].max, block.y);
            size[1].min = Math.min(size[1].min, block.y);

            size[2].max = Math.max(size[2].max, block.z);
            size[2].min = Math.min(size[2].min, block.z);
        }
        return size;
    }

    public static void spawnEntity(NativeBlockSource region, int x, int y, int z, String[] ents, Random random){
        BlockState state = region.getBlock(x, y, z);
        if(state.id == 52){
            NativeTileEntity tile = region.getBlockEntity(x, y, z);
            NativeCompoundTag tag = tile.getCompoundTag();
            tag.putString("EntityIdentifier", ents[random.nextInt(ents.length)]);
            tile.setCompoundTag(tag);
        }
    }

    private static int getDis(int x1, int y1, int z1, int x2, int y2, int z2){
        return (int) Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2) + Math.pow(z1-z2, 2));
    }
    
    public static void generateShapeOptimization(int xx, int yy, int zz, int r, BlockData block, NativeBlockSource region) {
        for (int y = yy - r; y <= yy + r; y++)
            for (int x = xx - r; x <= xx + r; x++)
                for (int z = zz - r; z <= zz + r; z++)
                    if (getDis(xx, yy, zz, x, y, z) * 1.5 < r * 1.5)
                        block.set(x, y, z, region);
    }

    public static void generateShape(int xx, int yy, int zz, int r, int y_max, BlockData block, int countDirt, BlockData dirt, BlockData grass, NativeBlockSource region){
        for(int y = yy-r;y <= yy+r;y++){
            for(int x = xx-r;x <= xx+r;x++)
                for(int z = zz-r;z <= zz+r;z++){
                    if(getDis(xx, yy, zz, x, y, z) * 1.5 < r * 1.5)
                        if(y == (yy-r) + y_max + 1)
                            grass.set(x,  y, z, region);
                        else if(y >= ((yy-r) + y_max)-countDirt+1)
                            dirt.set(x,  y, z, region);
                        else
                            block.set(x, y, z, region);
                }
            if(y > (yy-r) + y_max)
                break;
        }
    }

    public static void fill(int x1, int y1, int z1, int x2, int y2, int z2, BlockData block, NativeBlockSource region){
        for(int x = Math.min(x1, x2);x < Math.max(x1, x2);x++)
            for(int y = Math.min(y1, y2);y < Math.max(y1, y2);y++)
                for(int z = Math.min(z1, z2);z < Math.max(z1, z2);z++)
                    block.set(x, y, z, region);
    }

    public interface IHandler {
        boolean isBuildBlock(int x, int y, int z, NativeBlockSource region);
    }

    public static void fill(int x1, int y1, int z1, int x2, int y2, int z2, BlockData block, NativeBlockSource region, IHandler handler){
        for(int x = Math.min(x1, x2);x < Math.max(x1, x2);x++)
            for(int y = Math.min(y1, y2);y < Math.max(y1, y2);y++)
                for(int z = Math.min(z1, z2);z < Math.max(z1, z2);z++)
                    if(handler.isBuildBlock(x, y, z, region))
                        block.set(x, y, z, region);
    }
}
