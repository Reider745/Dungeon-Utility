package com.reider.dungeonutility.struct.generation.thread.algorithms;

import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public class ClearingClusters implements Base {
    public boolean enable = true;
    public int activation = 50;
    public int radius = 20;

    @Override
    public void run(Vector3 pos, WorldStructure[] structures) {
        int size = structures.length;
        if(enable && size % activation == 0){
            for(int i = 0;i < size;i++){
                WorldStructure gl = structures[i];
                if(gl==null)
                    continue;
                for(int j = 0;j < size;j++){
                    WorldStructure structure = structures[i];
                    if(structure!=null&&structure.pos.distance(gl.pos) <= radius)
                        structures[i] = null;
                }
            }
        }
    }
}
