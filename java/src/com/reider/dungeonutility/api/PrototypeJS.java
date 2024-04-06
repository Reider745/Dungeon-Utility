package com.reider.dungeonutility.api;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap;


public class PrototypeJS implements StructurePrototypeInterface {
    public ScriptObjectWrap setBlockFunc;
    public ScriptObjectWrap isSetBlockFunc;
    public ScriptObjectWrap afterFunc;
    public ScriptObjectWrap beforeFunc;

    public PrototypeJS(Object isSetBlock, Object setBlock, Object beforeFunc, Object afterFunc){
        
        this.isSetBlockFunc = ScriptObjectWrap.create(isSetBlock);
        this.setBlockFunc = ScriptObjectWrap.create(setBlock);
        this.afterFunc = ScriptObjectWrap.create(afterFunc);
        this.beforeFunc = ScriptObjectWrap.create(beforeFunc);
    }
    @Override
    public Boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        return isSetBlockFunc.invokeAsBooleanFunc(null, new Object[] {orgPos, data, region, packet}, true);
    }
    @Override
    public void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        setBlockFunc.invokeAsVoidFunc(null, new Object[] {orgPos, data, region, packet});
    }
    @Override
    public void after(int x, int y, int z, NativeBlockSource region, Object packet){
        afterFunc.invokeAsVoidFunc(afterFunc, new Object[] {x, y, z, region, packet});
    }
    public void before(int x, int y, int z, NativeBlockSource region, Object packet){
        beforeFunc.invokeAsVoidFunc(null, new Object[] {x, y, z, region, packet});
    }
}
