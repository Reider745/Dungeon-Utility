package com.reider.dungeonutility.multiversions.wrap;

import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.multiversions.js_types.IJsPass;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
//import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap;

public class WrapJsObject implements IJsObject {
    //private final ScriptObjectWrap wrap;

    public WrapJsObject(Object js){
       // wrap = ScriptObjectWrap.create(js);
    }

    @Override
    public void setJavaObj(String name, Object value) {
        //wrap.setJavaObj(name, value);
    }

    @Override
    public Object getJavaObj(String name) {
        return null;
        //return wrap.getJavaObj(name);
    }

    @Override
    public void setScriptObj(String name, IJsPass value) {
        //wrap.setScriptObj(name, ScriptObjectWrap.create(value.passToScript()));
    }

    @Override
    public IJsPass getScriptObj(String name) {
       /* final ScriptObjectWrap _wrap = wrap.getScriptObj(name);
        final Object pass = _wrap.passToScript();

        if(ScriptObjectWrap.isScriptObj(pass))
            return new WrapJsObject(pass);
        else if(_wrap.isArray())
            return new WrapJsArray(pass);

        Logger.warning("Ret null getScriptObj, "+(pass == null ? "null" : pass.getClass()));*/
        return null;
    }

    @Override
    public int getInt(String name, int def) {
        return 0;
        //return wrap.getInt(name, def);
    }

    @Override
    public void setInt(String name, int val) {
        //wrap.setInt(name, val);
    }

    @Override
    public void setString(String name, String val) {
        //wrap.setString(name, val);
    }

    @Override
    public Object passToScript() {
        return null;
        //return wrap.passToScript();
    }
}
