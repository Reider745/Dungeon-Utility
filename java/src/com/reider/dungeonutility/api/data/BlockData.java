package com.reider.dungeonutility.api.data;

import com.reider.dungeonutility.api.StateManager;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeTileEntity;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class BlockData implements Cloneable {
    private interface ISetBlock {
        void set(BlockData data, int x, int y, int z, NativeBlockSource region);
    }

    public int x;
    public int y;
    public int z;

    public BlockState state;
    public BlockState stateExtra;
    public NativeCompoundTag tag;

    private static final Collection<ISetBlock> EMPTY = new HashSet<>();
    private Collection<ISetBlock> list = EMPTY;

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

        set(data.list);
    }

    public BlockData(int x, int y, int z, BlockData data){
        this(x, y, z, data.state, data.stateExtra, data.tag);
        set(data.list);
    }

    public BlockData set(Collection<ISetBlock> list){
        this.list = list;
        return this;
    }

    public BlockData copy(){
        return new BlockData(this);
    }

    @Override
    protected BlockData clone(){
        return this.copy();
    }

    public void set(int X, int Y, int Z, NativeBlockSource region){
        for(ISetBlock func : list)
            func.set(this, X, Y, Z, region);
        /*region.setBlock(X + x, Y + y, Z + z, state);

        if(stateExtra != null)
            region.setExtraBlock(X + x, Y + y, Z + z, stateExtra);

        final NativeTileEntity tile = region.getBlockEntity(x + X, y + Y, z + Z);
        if(tile != null && tag != null)
            tile.setCompoundTag(tag);*/
    }

    protected boolean checkState(BlockState state1, BlockState state2){
        return (state1 == null && state2 == null) || !(state1 != null && state2 != null) || ((state2.runtimeId == -1 || state1.runtimeId == -1) ?
                (state1.id == state2.id && state1.data == state2.data) :
                state1.runtimeId == state2.runtimeId);
    }

    public boolean isBlock(int X, int Y, int Z, NativeBlockSource region){
        return checkState(region.getBlock(X + x, Y + y, Z + z), state) && checkState(region.getExtraBlock(X + x, Y + y, Z + z), stateExtra);
    }

    public String getName(){
        return "BlockData";
    }

    public BlockData getData() {
        return new BlockData(x, y, z,
                state == null ? StateManager.EMPTY_STATE : state,
                stateExtra == null ? StateManager.EMPTY_STATE : stateExtra,
                tag == null ? new NativeCompoundTag() : tag);
    }

    public boolean isState(){
        return state != null;
    }

    public boolean isExtra(){
        return stateExtra != null;
    }

    public boolean isTag(){
        return tag != null;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BlockData)) return false;
        BlockData data = ((BlockData) obj).getData();
        return x == data.x && y == data.y && z == data.z && data.state.equals(state) && data.stateExtra.equals(stateExtra) && data.tag.equals(tag);
    }

    @Override
    public String toString() {
        return "BlockData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", state=" + state +
                ", stateExtra=" + stateExtra +
                ", tag=" + tag +
                '}';
    }

    private static final BlockDataEmpty EMPTY_BLOCK_DATA = new BlockDataEmpty();

    public static BlockData createData(int x, int y, int z, BlockState block, BlockState extra, NativeCompoundTag tag){
        //Logger.debug(x+" "+y+" "+z+"   "+(block != null) + "    "+(extra != null));
        final Collection<ISetBlock> list = new HashSet<>();

        if(block != null)
            list.add((data, x1, y1, z1, region) -> {
                region.setBlock(x + x1, y + y1, z + z1, data.state);
            });
        if(extra != null)
            list.add((data, x1, y1, z1, region) -> {
                region.setExtraBlock(x + x1, y + y1, z + z1, data.stateExtra);
            });
        if(tag != null)
            list.add((data, x1, y1, z1, region) -> {
                final NativeTileEntity tile = region.getBlockEntity(x + x1, y + y1, z + z1);
                if(tile != null) tile.setCompoundTag(data.tag);
            });

        if(block != null){
            if(block.id == 0 && extra != null && extra.id == 0)
                return new BlockDataAir(x, y, z)
                        .set(list);
            return new BlockData(x, y, z, block, extra, tag)
                    .set(list);
        }

        return EMPTY_BLOCK_DATA;
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
