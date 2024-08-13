package com.reider.dungeonutility.multiversions;

import com.reider.dungeonutility.multiversions.js_types.IJsArray;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.multiversions.js_types.IJsPass;
import com.zhekasmirnov.innercore.api.NativeItemInstanceExtra;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;

public interface  IPackVersion {
    IJsObject createObjectEmpty();
    IJsObject createObject(Object obj);
    IJsArray createArrayEmpty();
    void addCallback(String name, IJsFunctionImpl func);
    boolean canJSObject(Object obj);
    IJsFunctionImpl createForFunction(Object func);
    IJsPass createJavaFunction(IJsFunctionImpl func);
    IJsObject getContent(UIWindow window);
    void setContent(UIWindow window, IJsObject jsObject);
    Object makeItemInstance(int id, int count, int data, NativeItemInstanceExtra extra);
}
