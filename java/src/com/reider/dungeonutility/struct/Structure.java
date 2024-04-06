package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.StructurePrototype;
import com.reider.dungeonutility.api.StructurePrototypeInterface;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class Structure {
    public static void setStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region, Object packat){
        stru.set(x, y, z, region);
    }
    public static void build(StructureDescription stru, int x, int y, int z, NativeBlockSource region, long slp, Object packat){
        stru.build(x, y, z, region, slp);
    }
    public static Boolean isStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        return stru.isStructure(x, y, z, region);
    }
    public static Boolean isSetStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        return stru.isSetStructure(x, y, z, region);
    }
    public static void destroy(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        stru.destroy(x, y, z, region);
    }
    public static void setGlobalPrototype(String name, StructurePrototypeInterface prot){
        StructureLoader.getStructurePool(StructureLoader.default_pool).setGlobalPrototype(name, prot);
    }
    public static StructurePrototypeInterface getGlobalPrototype(String name){
        return StructureLoader.getStructure(name).prot;
    }

    private StructureDescription stru;
    private StructurePrototypeInterface prot;
    private Boolean useGlobalProt;
    public Structure(StructureDescription stru){
        this.stru = stru;
        prot = new StructurePrototype();
        useGlobalProt = true;
    }

    public void setUseGlobalPrototype(Boolean useGlobalProt) {
        this.useGlobalProt = useGlobalProt;
    }

    public Boolean isUseGlobalPrototype() {
        return useGlobalProt;
    }

    public void setPrototype(StructurePrototypeInterface prot){
        this.prot = prot;
    }
    public StructurePrototypeInterface getPrototype(){
        return prot;
    }

    public StructureDescription getStructure() {
        return stru;
    }
    public void setStructure(StructureDescription stru){
        this.stru = stru;
    }


    public Boolean isStructure(int x, int y, int z, NativeBlockSource region) {
        return stru.isStructure(x, y, z, region);
    }
    public Boolean isSetStructure(int x, int y, int z, NativeBlockSource region){
        return stru.isSetStructure(x, y, z, region);
    }

    public void setStructure(int x, int y, int z, NativeBlockSource region, Object packet){
        stru.set(x, y, z, region, prot, useGlobalProt, packet);
    }
    public void build(int x, int y, int z, NativeBlockSource region, long spl, Object packet){
        try {
            stru.build(x, y, z, region, prot, useGlobalProt, packet, spl);
        }catch (InterruptedException e){}
    }

    public void destroy(int x, int y, int z, NativeBlockSource region){
        stru.destroy(x, y, z, region);
    }
}
