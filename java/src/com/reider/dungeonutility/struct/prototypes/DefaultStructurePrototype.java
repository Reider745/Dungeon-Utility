package com.reider.dungeonutility.struct.prototypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.items.ItemGeneration;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class DefaultStructurePrototype implements IStructurePrototype {
    private final String name;
    private final HashMap<Integer, ArrayList<BlockData>> blocks;

    public DefaultStructurePrototype(boolean isItems, String name, StructureDestructibility blocks){
        this.name = name;
        this.blocks = blocks.getMap();
    }

    public DefaultStructurePrototype(String name, StructureDestructibility blocks){
        this(false, name, blocks);
    }

    Random random_cache = null;
    public Random getRandom(Object packet){
        if(random_cache != null)
            return random_cache;
        Random random = null;

        final IPackVersion version = DungeonUtilityMain.getPackVersionApi();
        if(version.canJSObject(packet)){
            IJsObject scriptObjectWrap = version.createObject(packet);
            
            Object rand = scriptObjectWrap.getJavaObj("random");
            if(rand instanceof Random)
                random = (Random) rand;
            else
                random = new Random();
        }else
            random = new Random();
        random_cache = random;
        return random;
    }

    @Override
    public boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        ArrayList<BlockData> array = blocks.get(data.getData().state.id);
        if(array != null){
            Random random = getRandom(packet);
            array.get(random.nextInt(array.size())).set(((int) orgPos.x)+data.x,((int) orgPos.y)+data.y, ((int) orgPos.z)+data.z, region);
            return false;
        }
        return true;
    }

    @Override
    public void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        ItemGeneration.fill(name, ((int) orgPos.x)+data.x,((int) orgPos.y)+data.y, ((int) orgPos.z)+data.z, getRandom(packet), region, packet);
    }
}
