package com.reider.dungeonutility.multiversions.notwrap;

import com.reider.dungeonutility.multiversions.js_types.IJsArray;
import org.mozilla.javascript.NativeArray;

public class NotWrapJsArray implements IJsArray {
    private final NativeArray array;

    public NotWrapJsArray(NativeArray array){
        this.array = array;
    }

    @Override
    public Object passToScript() {
        return array;
    }
}
