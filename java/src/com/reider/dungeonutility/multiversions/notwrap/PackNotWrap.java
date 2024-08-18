package com.reider.dungeonutility.multiversions.notwrap;

import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsArray;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.multiversions.js_types.IJsPass;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeItemInstanceExtra;
import com.zhekasmirnov.innercore.api.commontypes.ItemInstance;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;
import com.zhekasmirnov.innercore.api.mod.util.ScriptableFunctionImpl;
import com.zhekasmirnov.innercore.api.runtime.Callback;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*
 Для паков которые не поддерживают ScriptObjectWrap
 */
public class PackNotWrap implements IPackVersion {
    private static final Method addCallbackMethod, getContentMethod, setContentMethod;
    private static final Constructor<?> constructorItemInstance;

    static {
        final Class<?> callbackClass = Callback.class;
        final Class<?> windowClass = UIWindow.class;
        final Class<?> itemInstanceClass = ItemInstance.class;

        try {
            addCallbackMethod = callbackClass.getMethod("addCallback", String.class, Function.class, int.class);
            getContentMethod = windowClass.getMethod("getContent");
            setContentMethod = windowClass.getMethod("setContent", ScriptableObject.class);
            constructorItemInstance = itemInstanceClass.getConstructor(int.class, int.class, int.class, NativeItemInstanceExtra.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IJsObject createObjectEmpty() {
        return new NotWrapJsObject(ScriptableObjectHelper.createEmpty());
    }

    @Override
    public IJsObject createObject(Object obj) {
        if(obj instanceof ScriptableObject)
            return new NotWrapJsObject((ScriptableObject) obj);
        else if(obj instanceof IJsObject)
            return (IJsObject) obj;
        Logger.warning("Not create NotWarpJsObject, "+(obj == null ? "null" : obj.getClass()));
        return null;
    }

    @Override
    public IJsArray createArrayEmpty() {
        return new NotWrapJsArray(ScriptableObjectHelper.createEmptyArray());
    }

    @Override
    public void addCallback(String name, IJsFunctionImpl func, int priority) {
        try {
            addCallbackMethod.invoke(null, name, (Function) this.createJavaFunction(func).passToScript(), priority);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canJSObject(Object obj) {
        return obj instanceof ScriptableObject;
    }

    @Override
    public IJsFunctionImpl createForFunction(Object func) {
        if(func instanceof Function){
            final Function _func = (Function) func;
            return (args -> {
                Context context = Context.getCurrentContext();
                if(context == null){
                    context = Context.enter();
                    context.setOptimizationLevel(9);
                    context.setLanguageVersion(200);
                }

                final Scriptable parent = _func.getParentScope();
               return _func.call(context, parent, parent, args);
            });
        }
        Logger.warning("Not create Func, "+(func == null ? "null" : func.getClass()));
        return null;
    }

    @Override
    public IJsPass createJavaFunction(IJsFunctionImpl func) {
        return () -> new ScriptableFunctionImpl(){
            @Override
            public Object call(Context context, Scriptable scriptable, Scriptable scriptable1, Object[] args) {
                return func.call(args);
            }
        };
    }

    @Override
    public IJsObject getContent(UIWindow window) {
        try {
            return new NotWrapJsObject((ScriptableObject) getContentMethod.invoke(window));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setContent(UIWindow window, IJsObject jsObject) {
        try {
            setContentMethod.invoke(window, jsObject.passToScript());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object makeItemInstance(int id, int count, int data, NativeItemInstanceExtra extra) {
        try {
            return constructorItemInstance.newInstance(id, count, data, extra);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
