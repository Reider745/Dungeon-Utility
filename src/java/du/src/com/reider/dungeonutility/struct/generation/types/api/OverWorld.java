package com.reider.dungeonutility.struct.generation.types.api;

import com.reider.dungeonutility.struct.generation.types.IGenerationType;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.Random;

public class OverWorld implements IGenerationType {
    @Override
    public String getType() {
        return "OverWorld";
    }

    @Override
    public boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region) {
        return dimension == 0;
    }

    @Override
    public Vector3 getPosition(int x, int z, Random random, int dimension, NativeBlockSource region) {
        int X = x*16+random.nextInt(16);
        int Z = z*16+random.nextInt(16);
        return new Vector3(X, AdaptedScriptAPI.GenerationUtils.findSurface(X, 80, Z), Z);
    }
}
