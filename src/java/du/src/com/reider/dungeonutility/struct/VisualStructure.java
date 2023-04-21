package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.innercore.api.NativeRenderMesh;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;


public class VisualStructure {
    private static int convertId(int id){
        if(id > 255 && id < 2048)
            return 255 - id;
        return id;
    }

    public static NativeRenderMesh getStructureMesh(String name){
        BlockData[] blocks = StructureLoader.getStructure(name).blocks;
        NativeRenderMesh mesh = AdaptedScriptAPI.ItemModel.getEmptyMeshFromPool();
        for(BlockData block : blocks)
            AdaptedScriptAPI.ItemModel.getForWithFallback(convertId(block.state.id), block.state.data).addToMesh(mesh, block.x, block.y, block.z);
        return mesh;
    }
};