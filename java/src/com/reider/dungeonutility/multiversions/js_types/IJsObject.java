package com.reider.dungeonutility.multiversions.js_types;

public interface IJsObject extends IJsPass {
    void setJavaObj(String name, Object value);
    Object getJavaObj(String name);
    void setScriptObj(String name, IJsPass value);
    IJsPass getScriptObj(String name);
    int getInt(String name, int def);
    void setInt(String name, int val);
    void setString(String name, String val);
}
