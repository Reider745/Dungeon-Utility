package com.reider.dungeonutility.struct.generation.types;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.Random;

public class Nether extends OverWorld {
    private int min;
    private int max;
    private int doorstep;
    public Nether(int min, int max, int doorstep){
        this.min = min;
        this.max = max;
    }
    public Nether(int min, int max){
        this(min, max, min);
    }
    public Nether(){
        this(10, 200, 0);
    }
    @Override
    public String getType() {
        return "Nether";
    }

    @Override
    public boolean isGeneration(Vector3 pos, Random random, int dimension, NativeBlockSource region) {
        return dimension == 1 && pos.y > doorstep;
    }

    @Override
    public Vector3 getPosition(int x, int z, Random random, int dimension, NativeBlockSource region) {
        int X = x*16+random.nextInt(16);
        int Z = z*16+random.nextInt(16);
        return new Vector3(X, AdaptedScriptAPI.GenerationUtils.findSurface(X, random.nextInt(max - min) + min, Z), Z);
    }
}
