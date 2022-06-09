package com.reider.dungeonutility.items;

import com.zhekasmirnov.innercore.api.commontypes.ItemInstance;

import java.util.HashMap;

public class ItemGeneration {
    private static HashMap<String, Generator> generators = new HashMap();
    public static void newGenerator(String name, Generator generator){
        generators.put(name, generator);
    }
    public static void addItem(ItemInstance instance){

    }
    public static void setPrototype(){

    }
    public static void fill(){

    }
}
