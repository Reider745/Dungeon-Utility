package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.UtilAnimation;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.innercore.api.NativeItemModel;
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

    private static final NativeRenderMesh EMPTY = new NativeRenderMesh();

    public static NativeRenderMesh getMeshForState(BlockState state, boolean is_added_air){
        if(state == null || (state.id == 0 && is_added_air)) return EMPTY;
        return NativeItemModel.getItemRenderMeshFor(state.id, 1, state.data, false);
    }

    public static NativeRenderMesh buildStructureMesh(StructureDescription stru, float size, boolean is_added_air){
        NativeRenderMesh mesh = new NativeRenderMesh();
        BlockData[] blocks = stru.blocks;
        for(BlockData block : blocks){
            mesh.addMesh(getMeshForState(block.state, is_added_air), block.x, block.y, block.z);
            mesh.addMesh(getMeshForState(block.stateExtra, is_added_air), block.x, block.y, block.z);
        }
        return mesh;
    }

    public static class AnimationOptimization {
        private UtilAnimation animation = null;

    }

    public static class Animation {

    }
};