package com.reider.dungeonutility.api;

import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

public class PrototypeJS implements StructurePrototypeInterface{
    private static Context context = Context.enter();
    private static ScriptableObject scope = context.initStandardObjects();

    public Function setBlockFunc;
    public Function isSetBlockFunc;
    public Function afterFunc;
    public Function beforeFunc;
    @JSConstructor
    public PrototypeJS(Function isSetBlock, Function setBlock, Function beforeFunc, Function afterFunc){
        this.isSetBlockFunc = isSetBlock;
        this.setBlockFunc = setBlock;
        this.afterFunc = afterFunc;
        this.beforeFunc = beforeFunc;
    }
    @Override
    public Boolean isBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        return (Boolean) isSetBlockFunc.call(context, scope, scope, new Object[] {orgPos, data, region, packet});
    }
    @Override
    public void setBlock(Vector3 orgPos, BlockData data, NativeBlockSource region, Object packet){
        setBlockFunc.call(context, scope, scope, new Object[] {orgPos, data, region, packet});
    }
    @Override
    public void after(int x, int y, int z, NativeBlockSource region, Object packet){
        afterFunc.call(context, scope, scope, new Object[] {x, y, z, region, packet});
    }
    public void before(int x, int y, int z, NativeBlockSource region, Object packet){
        beforeFunc.call(context, scope, scope, new Object[] {x, y, z, region, packet});
    }
}
