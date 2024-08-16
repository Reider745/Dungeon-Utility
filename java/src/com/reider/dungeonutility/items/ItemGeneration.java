package com.reider.dungeonutility.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.reider.dungeonutility.DUBoot;
import com.reider.dungeonutility.NativeAPI;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI.Callback;

public class ItemGeneration {
    public static HashMap<String, Generator> generators = new HashMap<>();
    public static ArrayList<Generator.ItemGen> integrations = new ArrayList<>();
    public static void newGenerator(String name){
        register(name, new Generator());
    }
    public static void setItemIntegration(Generator.ItemGen item){
        integrations.add(item);
    }
    public static void setFillEmpty(String name, boolean value){
        generators.get(name).infinityFill = value;
    }
    public static boolean isFillEmpty(String name){
        return generators.get(name).infinityFill;
    }
    public static void register(String name, Generator generator){
        generators.put(name, generator);
        DUBoot.getPackVersionApi()
                .addCallback("ModsLoaded", args -> {
                    for(Generator.ItemGen item : integrations)
                        ItemGeneration.addItem(name, item);
                    return null;
                });
    }
    public static void setItems(String name, ArrayList<Generator.ItemGen> items){
        generators.get(name).items = items;
    }
    public static ArrayList<Generator.ItemGen> getItems(String name){
        return generators.get(name).items;
    }
    public static void addItem(String name, Generator.ItemGen instance){
        if(generators.get(name) == null){
            NativeAPI.addItem(name, instance.getId(), instance.getData(), instance.getChance(), instance.getMin(), instance.getMax());
            return;
        }
        generators.get(name).addItem(instance);
    }
    public static boolean isGenerator(String name) {
        return generators.get(name) != null;
    }
    public static void setPrototype(String name, IPrototype prot){
        generators.get(name).setPrototype(prot);
    }
    public static IPrototype getPrototype(String name){
        return generators.get(name).getPrototype();
    }
    public static void fill(String name, int x, int y, int z, Random random, NativeBlockSource region, Object packet){
        if(generators.get(name) == null){
            NativeAPI.fill(name, x, y, z, region);
            return;
        }
        generators.get(name).fill(x, y, z, random, region, packet);
    }
}
