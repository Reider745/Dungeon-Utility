package com.reider.dungeonutility.struct.loaders;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.LoaderType;
import com.reider.dungeonutility.struct.prototypes.IStructurePrototype;
import com.reider.dungeonutility.struct.IStructureCopy;
import com.reider.dungeonutility.struct.StructureRotation;
import com.reider.dungeonutility.struct.StructureUtility;
import com.reider.dungeonutility.struct.formats.StructureCompression;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.ArrayList;
import java.util.HashMap;

public class StructurePool {
    private static final String[] EMPTY_STRING = new String[0];

    private final HashMap<String, StructureDescription> structures = new HashMap<>();
    private final StructureRegisterLoader loader = new StructureRegisterLoader(this);
    private final String name_pool;
    private String path = null;

    public StructurePool(String name, boolean global){
        this.name_pool = name;
        if(global) StructureLoader.registerPool(this);
        else
            DungeonUtilityMain.getPackVersionApi()
                    .addCallback("StructurePreLoad", args -> {
                        loader.loaded();
                        return null;
                    });
    }

    public StructurePool(String name){
        this(name, true);
    }

    public String getName(){
        return name_pool;
    }

    public StructureRegisterLoader getLoader() {
        return loader;
    }

    public void setPathStructures(String path){
        this.path = path;
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
        return structures.keySet().toArray(EMPTY_STRING);
    }

    public void loadRuntime(String name, String path, String type, boolean compression){
        if(compression) {
            setStructure(name, LoaderType.getType(type).read(StructureCompression.decompression(path).getBytes(), path));
        } else {
            setStructure(name, LoaderType.getType(type).read(Utils.readFileBytes(path), path));
        }
    }

    public void load(String name, String path, String type, boolean compile){
        if(path == null || path.isEmpty()){
            final String[] dirs = name.replaceAll("\\\\\\\\", "/").split("/");
            load(dirs[dirs.length-1], this.path+"/"+name+".struct", type, compile);
            return;
        }

        if(LoaderType.getType(type).isLoadRuntime()) {
            loadRuntime(name, path, type, compile);
            return;
        }

        loader.add(name, path, type, compile);
    }

    public void copy(String name1, String name2, IStructureCopy copy){
        final  StructureDescription structure = getStructure(name1);
        final ArrayList<BlockData> newBlocks = new ArrayList<>();

        for(BlockData block : structure.blocks)
            newBlocks.add(copy.copyBlock(block));

        this.setStructure(name2, new StructureDescription(newBlocks, copy.copyPrototype(structure.prot)));
    }

    public void registerRotations(StructureDescription stru, String name, StructureRotation[] rotates) {
        StructureUtility.registerRotations(this, stru, name, rotates);
    }

    public void setGlobalPrototype(String name, IStructurePrototype prot){
        StructureDescription stru = getStructure(name);
        stru.prot = prot;
        setStructure(name, stru);
    }
}
