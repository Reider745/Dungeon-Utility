package com.reider.dungeonutility.struct.generation.types.api;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.Random;

public class Default extends OverWorld {
    @Override
    public String getType() {
        return "default";
    }

    @Override
    public boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region) {
        return true;
    }
}
