package com.reider.dungeonutility;

import com.reider.dungeonutility.api.StructurePrototypeInterface;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.ArrayList;

public class NativeAPI {
    public native static void setCustomStructure(String name, int x, int y, int z, long region);
    public static void setCustomStructure(String name, int x, int y, int z, NativeBlockSource region){
        setCustomStructure(name, x, y, z, region.getPointer());
    }

    public native static boolean isCustomLoad(String name);

    public native static void addItem(String name, int id, int data, float cahnce, int min, int max);
    public native static void fill(String name, int x, int y, int z, long region);
    public static void fill(String name, int x, int y, int z, NativeBlockSource region){
        fill(name, x, y, z, region.getPointer());
    }

    private static ArrayList<StructurePrototypeInterface> features = new ArrayList<StructurePrototypeInterface>();
    public static void addFeature(StructurePrototypeInterface prototypeInterface){
        features.add(prototypeInterface);
    }


    public static void setBlockFeature(int x, int y, int z, int id, long point){
        for(StructurePrototypeInterface prot : features)
            prot.setBlock(new Vector3(x, y, z), new BlockData(x, y, z, new BlockState(id, 0)), NativeBlockSource.getFromCallbackPointer(point), new Object());
    }
    public static boolean isBlockFeature(int x, int y, int z, int id, long point){
        boolean value = true;
        for(StructurePrototypeInterface prot : features) {
            boolean input = prot.isBlock(new Vector3(x, y, z), new BlockData(x, y, z, new BlockState(id, 0)), NativeBlockSource.getFromCallbackPointer(point), new Object());
            if(value)
                value = input;
        }
        return value;
    }

}
