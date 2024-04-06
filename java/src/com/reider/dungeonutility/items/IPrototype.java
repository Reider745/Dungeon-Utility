package com.reider.dungeonutility.items;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import java.util.Random;

public interface IPrototype {
    public void before(Vector3 pos, NativeBlockSource region, Object packet);
	public void after(Vector3 pos, NativeBlockSource region, Object packet);
	public boolean isGenerate(Vector3 pos, float random, int slot, Object item, NativeBlockSource region, Random rand, Object packet);
	public void generate(Vector3 pos, float random, int slot, Object item, NativeBlockSource region, Random rand, Object packet);
}
