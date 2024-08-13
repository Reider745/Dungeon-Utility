package com.reider.dungeonutility.api;

import com.reider.dungeonutility.DUBoot;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;


public class PrototypeJS implements StructurePrototypeInterface {
    public IJsFunctionImpl setBlockFunc;
    public IJsFunctionImpl isSetBlockFunc;
    public IJsFunctionImpl afterFunc;
    public IJsFunctionImpl beforeFunc;

    public PrototypeJS(Object isSetBlock, Object setBlock, Object beforeFunc, Object afterFunc){
        final IPackVersion version = DUBoot.getPackVersionApi();

        this.isSetBlockFunc = version.createForFunction(isSetBlock);
        this.setBlockFunc = version.createForFunction(setBlock);
        this.afterFunc = version.createForFunction(afterFunc);
        this.beforeFunc = version.createForFunction(beforeFunc);
    }
    @Override
    public Boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        Object bool = isSetBlockFunc.call(new Object[] {orgPos, data, region, packet});
        if(bool instanceof Boolean)
            return ((Boolean) bool);
        return true;
    }
    @Override
    public void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        setBlockFunc.call(new Object[] {orgPos, data, region, packet});
    }
    @Override
    public void after(int x, int y, int z, NativeBlockSource region, Object packet){
        afterFunc.call(new Object[] {x, y, z, region, packet});
    }
    public void before(int x, int y, int z, NativeBlockSource region, Object packet){
        beforeFunc.call(new Object[] {x, y, z, region, packet});
    }
}
