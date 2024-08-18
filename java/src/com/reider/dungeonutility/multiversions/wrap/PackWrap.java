package com.reider.dungeonutility.multiversions.wrap;

import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsArray;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.multiversions.js_types.IJsPass;
import com.zhekasmirnov.innercore.api.NativeItemInstanceExtra;
import com.zhekasmirnov.innercore.api.commontypes.ItemInstance;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;
import com.zhekasmirnov.innercore.api.runtime.Callback;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap;

public class PackWrap implements IPackVersion {
    @Override
    public IJsObject createObjectEmpty() {
        return new WrapJsObject(ScriptObjectWrap.createNewEmptyObject());
    }

    @Override
    public IJsObject createObject(Object obj) {
        return new WrapJsObject(ScriptObjectWrap.create(obj));
    }

    @Override
    public IJsArray createArrayEmpty() {
        return new WrapJsArray(ScriptObjectWrap.createNewEmptyArray());
    }

    @Override
    public void addCallback(String name, IJsFunctionImpl func, int priority) {
        Callback.addCallback(name, ScriptObjectWrap.createJavaFunction(func::call), priority);
    }

    @Override
    public boolean canJSObject(Object obj) {
        return ScriptObjectWrap.isScriptObj(obj);
    }

    @Override
    public IJsFunctionImpl createForFunction(Object func) {
        ScriptObjectWrap wrap = ScriptObjectWrap.create(func);
        if(!wrap.isFunction())
            throw new RuntimeException("Error not function!");
        return (args) -> wrap.invokeAsJavaObjFunc(null, args).passToScript();
    }

    @Override
    public IJsPass createJavaFunction(IJsFunctionImpl func) {
        ScriptObjectWrap _func = ScriptObjectWrap.createJavaFunction(func::call);
        return _func::passToScript;
    }

    @Override
    public IJsObject getContent(UIWindow window) {
        return new WrapJsObject(window.getContentWrap());
    }

    @Override
    public void setContent(UIWindow window, IJsObject jsObject) {
        window.setContent(jsObject.passToScript());
    }

    @Override
    public Object makeItemInstance(int id, int count, int data, NativeItemInstanceExtra extra) {
        return ItemInstance.make(id, count, data, extra);
    }
}
