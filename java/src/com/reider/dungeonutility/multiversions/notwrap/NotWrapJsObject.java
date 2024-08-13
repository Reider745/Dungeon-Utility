package com.reider.dungeonutility.multiversions.notwrap;

import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.multiversions.js_types.IJsPass;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

public class NotWrapJsObject implements IJsObject {
    private final ScriptableObject obj;

    public NotWrapJsObject(ScriptableObject obj){
        this.obj = obj;
    }


    @Override
    public void setJavaObj(String name, Object value) {
        obj.put(name, obj, value);
    }

    @Override
    public Object getJavaObj(String name) {
        return obj.get(name, obj);
    }

    @Override
    public void setScriptObj(String name, IJsPass value) {
        obj.put(name, obj, value.passToScript());
    }

    @Override
    public IJsPass getScriptObj(String name) {
        Object val = obj.get(name, obj);
        if(val instanceof NativeArray)
            return new NotWrapJsArray((NativeArray) val);
        else if(val instanceof ScriptableObject)
            return new NotWrapJsObject((ScriptableObject) val);
        Logger.warning("Ret null getScriptObj, "+(obj == null ? "null" : obj.getClass()));
        return null;
    }

    @Override
    public int getInt(String name, int def) {
        Object val = obj.get(name, obj);
        if(val instanceof Number)
            return ((Number) val).intValue();
        return def;
    }

    @Override
    public void setInt(String name, int val) {
        obj.put(name, obj, val);
    }

    @Override
    public void setString(String name, String val) {
        obj.put(name, obj, val);
    }

    @Override
    public Object passToScript() {
        return obj;
    }
}
