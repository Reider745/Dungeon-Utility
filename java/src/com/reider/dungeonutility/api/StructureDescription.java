package com.reider.dungeonutility.api;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class StructureDescription {
    public StructureDescription(BlockData[] blocks, StructurePrototypeInterface prot){
        this.blocks = blocks;
        this.prot = prot;
    }
    public StructureDescription(StructureDescription stru){
        this(stru.blocks.clone(), stru.prot);
    }
    public StructureDescription(BlockData[] blocks){
        this(blocks, new StructurePrototype());
    }
    public BlockData[] blocks;
    public StructurePrototypeInterface prot;
    public StructureDescription copy(){
        return new StructureDescription(this);
    }
    public void set(int x, int y, int z, NativeBlockSource region, StructurePrototypeInterface addProt, Boolean use, Object packet){
        if(!use) {
            addProt.before(x, y, z, region, packet);
            for (BlockData block : blocks) {
                if (addProt.isBlock(new Vector3(x, y, z), block, region, packet))
                    block.set(x, y, z, region);
                addProt.setBlock(new Vector3(x, y, z), block, region, packet);
            }
            addProt.after(x, y, z, region, packet);
        }else{
            prot.before(x, y, z, region, packet);
            addProt.before(x, y, z, region, packet);
            for (BlockData block : blocks) {
                if (prot.isBlock(new Vector3(x, y, z), block, region, packet) && addProt.isBlock(new Vector3(x, y, z), block, region, packet))
                    block.set(x, y, z, region);
                prot.setBlock(new Vector3(x, y, z), block, region, packet);
                addProt.setBlock(new Vector3(x, y, z), block,  region, packet);
            }
            addProt.after(x, y, z, region, packet);
            prot.after(x, y, z, region, packet);
        }
    }
    public void build(int x, int y, int z, NativeBlockSource region, StructurePrototypeInterface addProt, Boolean use, Object packet, long slp) throws InterruptedException{
        if(!use) {
            addProt.before(x, y, z, region, packet);
            for (BlockData block : blocks) {
                if (addProt.isBlock(new Vector3(x, y, z), block, region, packet)) {
                    block.set(x, y, z, region);
                    Thread.sleep(slp);
                }
                addProt.setBlock(new Vector3(x, y, z), block, region, packet);
            }
            addProt.after(x, y, z, region, packet);
        }else{
            prot.before(x, y, z, region, packet);
            addProt.before(x, y, z, region, packet);
            for (BlockData block : blocks) {
                if (prot.isBlock(new Vector3(x, y, z), block, region, packet) && addProt.isBlock(new Vector3(x, y, z), block, region, packet)) {
                    block.set(x, y, z, region);
                    Thread.sleep(slp);
                }
                prot.setBlock(new Vector3(x, y, z), block, region, packet);
                addProt.setBlock(new Vector3(x, y, z), block,  region, packet);
            }
            addProt.after(x, y, z, region, packet);
            prot.after(x, y, z, region, packet);
        }
    }
    public void set(int x, int y, int z, NativeBlockSource region){
        set(x, y, z, region, new StructurePrototype(), true, new Object());
    }
    public void build(int x, int y, int z, NativeBlockSource region, long spl){
        try {
            build(x, y, z, region, new StructurePrototype(), true, new Object(), spl);
        }catch (InterruptedException e){}
    }
    public Boolean isSetStructure(int x, int y, int z, NativeBlockSource region){
        for(BlockData block : blocks){
            BlockState state = region.getBlock(x+ block.x, y+ block.y,z+ block.z);
            if(state.id != 0 || block.getData().state.id == state.id)
                return false;
        }
        return true;
    }
    public Boolean isStructure(int x, int y, int z, NativeBlockSource region){
        for(BlockData block : blocks)
            if(!block.isBlock(x, y, z, region))
                return false;
        return true;
    }
    public void destroy(int x, int y, int z, NativeBlockSource region){
        for (BlockData block : blocks)
            region.setBlock(x + block.x, y + block.y, z + block.z, 0, 0);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof StructureDescription) ) return false;
        return ((StructureDescription) obj).blocks.equals(this.blocks);
    }
}
