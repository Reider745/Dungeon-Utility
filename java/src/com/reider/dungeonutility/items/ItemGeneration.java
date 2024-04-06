package com.reider.dungeonutility.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.reider.dungeonutility.NativeAPI;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI.Callback;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap.ScriptFunctionImpl;

public class ItemGeneration {
    public static HashMap<String, Generator> generators = new HashMap<>();
    public static ArrayList<Generator.ItemGen> integrations = new ArrayList<>();
    public static void newGenerator(String name){
        register(name, new Generator());
    }
    public static void setItemIntegration(Generator.ItemGen item){
        integrations.add(item);
    }
    public static void register(String name, Generator generator){
        generators.put(name, generator);
        Callback.addCallback(name, generator, 0);
        Callback.addCallback("ModsLoaded", ScriptObjectWrap.createJavaFunction(new ScriptFunctionImpl() {
            @Override
            public Object call(Object[] arg0) {
                for(Generator.ItemGen item : integrations)
                    ItemGeneration.addItem(name, item);
                return null;
            }
            
        }), 0);
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
