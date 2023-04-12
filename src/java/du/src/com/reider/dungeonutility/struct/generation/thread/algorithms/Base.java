package com.reider.dungeonutility.struct.generation.thread.algorithms;

import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;

public interface Base {
    void run(Vector3 pos, WorldStructure[] structures);
}
