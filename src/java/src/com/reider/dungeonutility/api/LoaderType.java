package com.reider.dungeonutility.api;

public class LoaderType implements LoaderTypeInterface {
    @Override
    public boolean isLoadRuntime() {
        return false;
    }

    @Override
    public StructureDescription read(String file, String path){
        return null;
    }
    @Override
    public String save(StructureDescription stru){
        return "[]";
    }
}
