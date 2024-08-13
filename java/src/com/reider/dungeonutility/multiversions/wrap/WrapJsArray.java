package com.reider.dungeonutility.multiversions.wrap;

import com.reider.dungeonutility.multiversions.js_types.IJsArray;

public class WrapJsArray implements IJsArray {
    private final Object array;

    public WrapJsArray(Object array){
        this.array = array;
    }

    @Override
    public Object passToScript() {
        return array;
    }
}
