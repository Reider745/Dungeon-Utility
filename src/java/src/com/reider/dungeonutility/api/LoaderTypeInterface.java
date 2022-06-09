package com.reider.dungeonutility.api;

public interface LoaderTypeInterface {
    StructureDescription read(String file, String path);
    String save(StructureDescription stru);
    boolean isLoadRuntime();
}
