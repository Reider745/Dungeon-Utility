package com.reider.dungeonutility.struct.generation.thread.algorithms;

import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public class CriticalRelease implements Base {
    public boolean enable = true;
    public int activation = 500;
    public int radius = 1000;

    @Override
    public void run(Vector3 pos, WorldStructure[] structures) {
        if(enable){
            int size = structures.length;
            if(size > activation)
                for(int i = 0;i < size;i++){
                    Vector3 pos_structure = structures[i].pos;
                    if(pos.distance(pos_structure) > radius)
                        structures[i] = null;
                }
        }
    }
}