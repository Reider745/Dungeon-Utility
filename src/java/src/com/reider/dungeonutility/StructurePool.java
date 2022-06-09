package com.reider.dungeonutility;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.StructurePrototypeInterface;
import com.reider.dungeonutility.struct.StructureCopy;
import com.reider.dungeonutility.struct.StructureRotation;
import com.reider.dungeonutility.struct.StructureUtility;
import com.reider.dungeonutility.struct.formats.StructureCompile;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.HashMap;

public class StructurePool {
    private HashMap<String, StructureDescription> structures;
    private String pool;
    public StructurePool(String name, boolean global){
        structures = new HashMap();
        if(global)
            StructureLoader.pools.put(name, this);
        this.pool = name;
    }

    public StructurePool(String name){
        this(name, true);
    }
    public boolean isLoad(String name){
        return structures.containsKey(name);
    }

    public void deLoad(String name){
        structures.remove(name);
    }

    public StructureDescription getStructure(String name){
        return structures.get(name);
    }

    public void setStructure(String name, StructureDescription stru){
        structures.put(name, stru);
    }

    public HashMap<String, StructureDescription> getStructures() {
        return structures;
    }

    public String[] getAllStructure(){
        return structures.keySet().toArray(new String[structures.size()]);
    }

    public void loadRuntime(String name, String path, String type, Boolean compile){
        if(compile)
            setStructure(name, StructureLoader.getType(type).read(StructureCompile.decompile(path), path));
        else
            setStructure(name, StructureLoader.getType(type).read(StructureLoader.getTextToFile(path), path));
    }

    public void load(String name, String path, String type, Boolean compile){
        StructureLoader.load(pool, name, path, type, compile);
    }

    public void copy(String name1, String name2, StructureCopy c){
        StructureUtility.copy(pool, name1, name2, c);
    }
    public void registerRotations(StructureDescription stru, String name, StructureRotation[] rotates) {
        StructureUtility.registerRotations(pool, stru, name, rotates);
    }
    public void setGlobalPrototype(String name, StructurePrototypeInterface prot){
        StructureDescription stru = getStructure(name);
        stru.prot = prot;
        setStructure(name, stru);
    }
}
