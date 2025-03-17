package com.reider.dungeonutility;

import com.reider.dungeonutility.struct.prototypes.IStructurePrototype;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

import java.util.ArrayList;

@Deprecated
public class NativeAPI {
    public static void setCustomStructure(String name, int x, int y, int z, long region){}
    public static void setCustomStructure(String name, int x, int y, int z, NativeBlockSource region){
        setCustomStructure(name, x, y, z, region.getPointer());
    }

    public static boolean isCustomLoad(String name) {return false;}
    public static void addItem(String name, int id, int data, float cahnce, int min, int max) {}
    public static void fill(String name, int x, int y, int z, long region) {}
    public static void fill(String name, int x, int y, int z, NativeBlockSource region){
        fill(name, x, y, z, region.getPointer());
    }

    private static ArrayList<IStructurePrototype> features = new ArrayList<IStructurePrototype>();
    public static void addFeature(IStructurePrototype prototypeInterface){
        features.add(prototypeInterface);
    }


    public static void setBlockFeature(int x, int y, int z, int id, long point){
        for(IStructurePrototype prot : features)
            prot.setBlock(new Vector3(x, y, z), new BlockData(x, y, z, new BlockState(id, 0)), NativeBlockSource.getFromCallbackPointer(point), new Object());
    }
    public static boolean isBlockFeature(int x, int y, int z, int id, long point){
        boolean value = true;
        for(IStructurePrototype prot : features) {
            boolean input = prot.isBlock(new Vector3(x, y, z), new BlockData(x, y, z, new BlockState(id, 0)), NativeBlockSource.getFromCallbackPointer(point), new Object());
            if(value)
                value = input;
        }
        return value;
    }
}
