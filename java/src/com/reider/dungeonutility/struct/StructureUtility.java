package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.api.StructureDescription;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeTileEntity;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

import java.util.ArrayList;
import java.util.Random;

public class StructureUtility {
    public static BlockData[] getBlocksByArrayList(ArrayList<BlockData> blocks){
        BlockData[] result = new BlockData[blocks.size()];
        for(int i = 0;i < blocks.size();i++)
            result[i] = blocks.get(i);
        return result;
    }
    public static ArrayList<BlockData> getArrayListByBlocks(BlockData[] blocks){
        ArrayList<BlockData> result = new ArrayList<BlockData>();
        for(BlockData block : blocks)
            result.add(block);
        return result;
    }
    public static StructureDescription rotate(StructureDescription stru, int rotate){
        BlockData[] result = new BlockData[stru.blocks.length];
        for(int i = 0;i < stru.blocks.length;i++){
            BlockData data = stru.blocks[i];
            switch(rotate){
                case 0:
                    result[i] = new BlockData(data.x, data.y, data.z,data);
                    break;
                case 1:
                    result[i] = new BlockData(-data.x, data.y, data.z,data);
                    break;
                case 2:
                    result[i] = new BlockData(-data.x, data.y, -data.z,data);
                    break;
                case 3:
                    result[i] = new BlockData(data.x, data.y, -data.z,data);
                    break;
                case 4:
                    result[i] = new BlockData(data.x, -data.y, data.z,data);
                    break;
                case 5:
                    result[i] = new BlockData(-data.x, -data.y, data.z,data);
                    break;
                case 6:
                    result[i] = new BlockData(-data.x, -data.y, -data.z,data);
                    break;
                case 7:
                    result[i] = new BlockData(data.x, -data.y, -data.z,data);
                    break;
                default:
                    result[i] = new BlockData(data.x, data.y, data.z,data);
                    break;
            }
        }
        return new StructureDescription(result);
    }
    public static StructureDescription rotate(StructureDescription stru, StructureRotation rotate){
        return rotate(stru, rotate.ordinal());
    }
    public static void registerRotations(String savePool, StructureDescription stru, String name, int[] rotates){
        for(int i : rotates) {
            StructureLoader.getStructurePool(savePool).setStructure(name + "_" + i, rotate(stru, i));
            AdaptedScriptAPI.Logger.Log("register rotate - " + name + "_" + i, "Dungeon Utility");
        }
    }
    public static void registerRotations(StructureDescription stru, String name, int[] rotates){
        registerRotations(StructureLoader.default_pool, stru, name, rotates);
    }
    public static void registerRotations(String savePool, StructureDescription stru, String name, StructureRotation[] rotates) {
        for (StructureRotation i : rotates) {
            StructureLoader.getStructurePool(savePool).setStructure(name + "_" + i, rotate(stru, i));
            AdaptedScriptAPI.Logger.Log("register rotate - " + name + "_" + i.name(), "Dungeon Utility");
        }
    }
    public static void registerRotations(StructureDescription stru, String name, StructureRotation[] rotates) {
        registerRotations(StructureLoader.default_pool, stru, name, rotates);
    }
    public static void newStructure(String name){
        StructureLoader.loadRuntime(name, new StructureDescription(new BlockData[] {}));
    }
    public static int getCountBlock(StructureDescription stru){
        return stru.blocks.length;
    }
    public static String[] getAllStructureName(){
        return StructureLoader.getAllStructureName();
    }
    public static void copy(String pool, String name1, String name2, StructureCopy copy) {
        StructureDescription stru = StructureLoader.getStructure(name1);
        BlockData[] blocks = stru.blocks;
        ArrayList<BlockData> newBlocks = new ArrayList<BlockData>();
        for(BlockData block : blocks)
            newBlocks.add(copy.copyBlock(block));
        StructureLoader.getStructurePool(pool).setStructure(name2, new StructureDescription(getBlocksByArrayList(newBlocks), copy.copyPrototype(stru.prot)));
        AdaptedScriptAPI.Logger.Log("copy " + name1 + ", create " + name2, "DungeonUtility");
    }
    public static void copy(String name1, String name2, StructureCopy c){
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
        public Size(int min, int max){
            this.min = min;
            this.max = max;
        }
        public int min;
        public int max;
        @Override
        public String toString() {
            return "{max:"+max+",min:"+min+"}";
        }
    }
    public static Size[] getStructureSize(StructureDescription structure){
        BlockData[] stru = structure.blocks;
        Size[] size = {new Size(0, 0),new Size(0, 0),new Size(0, 0)};
        for(int i = 0; i < stru.length;i++){
            BlockData block = stru[i];
            size[0].max = Math.max(size[0].max, block.x);
            size[0].min = Math.min(size[0].min, block.x);

            size[1].max = Math.max(size[0].max, block.y);
            size[1].min = Math.min(size[0].min, block.y);

            size[2].max = Math.max(size[0].max, block.z);
            size[2].min = Math.min(size[0].min, block.z);
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
