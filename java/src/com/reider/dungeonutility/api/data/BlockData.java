package com.reider.dungeonutility.api.data;

import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeTileEntity;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

public class BlockData {
    public BlockData(int x, int y, int z, BlockState block, BlockState extra, NativeCompoundTag tag){
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = block;
        this.stateExtra = extra;
        this.tag = tag;
    }
    public BlockData(int x, int y, int z, BlockState block, BlockState extra){
        this(x, y, z, block, extra, null);
    }
    public BlockData(int x, int y, int z, BlockState block){
        this(x, y, z, block, null, null);
    }
    public BlockData(int x, int y, int z){
        this(x, y, z, null, null, null);
    }
    public BlockData(BlockData data){
        this(data.x, data.y, data.z, data.state, data.stateExtra, data.tag);
    }
    public BlockData(int x, int y, int z, BlockData data){
        this(x, y, z, data.state, data.stateExtra, data.tag);
    }
    public int x;
    public int y;
    public int z;
    public BlockState state;
    public BlockState stateExtra;
    public NativeCompoundTag tag;
    public BlockData copy(){
        return new BlockData(this);
    }
    public void set(int X, int Y, int Z, NativeBlockSource region){
        region.setBlock(X + x, Y + y, Z + z, state);
        if(stateExtra != null)
            region.setExtraBlock(X + x, Y + y, Z + z, stateExtra);
        NativeTileEntity tile = region.getBlockEntity(x + X, y + Y, z + Z);
        if(tile != null && tag != null)
            tile.setCompoundTag(tag);
    }
    public Boolean isBlock(int X, int Y, int Z, NativeBlockSource region){
        return state.equals(region.getBlock(X + x, Y + y, Z + z)) && stateExtra.equals(region.getExtraBlock(X + x, Y + y, Z + z));
    }
    public String getName(){
        return "BlockData";
    }
    public BlockData getData() {
        return new BlockData(x, y, z, state == null ? new BlockState(0, 0) : state, stateExtra == null ? new BlockState(0, 0) : stateExtra, tag == null ? new NativeCompoundTag() : tag);
    }

    public Boolean isState(){
        return state != null;
    }

    public Boolean isExtra(){
        return stateExtra != null;
    }

    public Boolean isTag(){
        return tag != null;
    }

    public static BlockData createData(int x, int y, int z, BlockState block, BlockState extra, NativeCompoundTag tag){
        //Logger.debug(x+" "+y+" "+z+"   "+(block != null) + "    "+(extra != null));
        if(block != null){
            if(tag != null)
                return new BlockData(x, y, z, block, extra, tag);
            if(extra != null) {
                if(block.id == 0 && extra.id == 0)
                    return new BlockDataAir(x, y, z);
                if(extra.id != 0)
                    return new BlockData(x, y, z, block, extra, tag);
            }
            if(block.id == 0)
                return new BlockDataAir(x, y, z);
            return new BlockDataState(x, y, z, block);
        }
        return new BlockDataEmpty(x, y, z);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof BlockData)) return false;
        BlockData data = ((BlockData) obj).getData();
        return x == data.x && y == data.y && z == data.z && data.state.equals(state) && data.stateExtra.equals(stateExtra) && data.tag.equals(tag);
    }
    public static BlockData createData(int x, int y, int z, BlockState block, BlockState extra){
        return createData(x, y, z, block, extra, null);
    }
    public static BlockData createData(int x, int y, int z, BlockState block){
        return createData(x, y, z, block, null, null);
    }
    public static BlockData createData(int x, int y, int z){
        return createData(x, y, z, null, null, null);
    }
    public static BlockData getBlockByCoords(int x, int y, int z, NativeBlockSource region){
        return createData(x, y, z, region.getBlock(x, y, z), region.getExtraBlock(x , y, z), null);
    }
}
