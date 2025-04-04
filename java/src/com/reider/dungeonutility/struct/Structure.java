package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.struct.generation.stand.api.BaseStand;
import com.reider.dungeonutility.struct.loaders.StructureLoader;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.prototypes.IStructurePrototype;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

public class Structure {
    public static void setStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region, Object packat){
        stru.set(x, y, z, region);
    }
    public static void build(StructureDescription stru, int x, int y, int z, NativeBlockSource region, long slp, Object packat){
        stru.build(x, y, z, region, slp);
    }
    public static boolean isStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        return stru.isStructure(x, y, z, region);
    }
    public static boolean isSetStructure(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        return stru.isSetStructure(x, y, z, region);
    }
    public static void destroy(StructureDescription stru, int x, int y, int z, NativeBlockSource region){
        stru.destroy(x, y, z, region);
    }
    public static void setGlobalPrototype(String name, IStructurePrototype prot){
        StructureLoader.default_pool.setGlobalPrototype(name, prot);
    }
    public static IStructurePrototype getGlobalPrototype(String name){
        return StructureLoader.getStructure(name).prot;
    }

    private StructureDescription stru;
    private IStructurePrototype prot;
    private boolean useGlobalProt;
    protected BaseStand stand;

    public Structure(StructureDescription stru){
        this.stru = stru;
        prot = StructureDescription.EMPTY_PROTOTYPE;
        useGlobalProt = true;
    }

    public void setUseGlobalPrototype(boolean useGlobalProt) {
        this.useGlobalProt = useGlobalProt;
    }

    public boolean isUseGlobalPrototype() {
        return useGlobalProt;
    }

    public void setPrototype(IStructurePrototype prot){
        this.prot = prot;
    }
    public IStructurePrototype getPrototype(){
        return prot;
    }

    public StructureDescription getStructure() {
        return stru;
    }
    public void setStructure(StructureDescription stru){
        this.stru = stru;
    }


    public boolean isStructure(int x, int y, int z, NativeBlockSource region) {
        return stru.isStructure(x, y, z, region);
    }
    public boolean isSetStructure(int x, int y, int z, NativeBlockSource region){
        return stru.isSetStructure(x, y, z, region);
    }

    public void setStructure(int x, int y, int z, NativeBlockSource region, Object packet){
        stru.set(x, y, z, region, prot, useGlobalProt, packet);
        if(stand != null)
            stand.setStand(region, x, y, z);
    }
    public void build(int x, int y, int z, NativeBlockSource region, long spl, Object packet){
        try {
            stru.build(x, y, z, region, prot, useGlobalProt, packet, spl);
            if(stand != null)
                stand.setStand(region, x, y, z);
        }catch (InterruptedException e){}
    }

    public void destroy(int x, int y, int z, NativeBlockSource region){
        stru.destroy(x, y, z, region);
    }

    public void setStand(BaseStand stand) {
        this.stand = stand;
    }

    public BaseStand getStand() {
        return stand;
    }
}
