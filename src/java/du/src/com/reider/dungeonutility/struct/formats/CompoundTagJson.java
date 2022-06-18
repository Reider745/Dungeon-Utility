package com.reider.dungeonutility.struct.formats;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import com.google.gson_du.JsonArray;
import com.google.gson_du.JsonObject;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;
import com.zhekasmirnov.innercore.api.nbt.NativeListTag;
import com.zhekasmirnov.innercore.api.nbt.NbtDataType;
import com.google.gson_du.internal.LinkedTreeMap;

public class CompoundTagJson {
    public static JsonArray getArray(NativeListTag tag){
        JsonArray array = new JsonArray();
        for (int i = 0; i < tag.length(); i++) {
            JsonArray object = new JsonArray();
            switch (tag.getValueType(i)) {
                case NbtDataType.TYPE_BYTE:
                object.add(tag.getByte(i));
                break;
                case NbtDataType.TYPE_SHORT: 
                object.add(tag.getShort(i));
                break;
                case NbtDataType.TYPE_INT: 
                object.add(tag.getInt(i));
                break;
                case NbtDataType.TYPE_INT64: 
                object.add(tag.getInt64(i));
                break;
                case NbtDataType.TYPE_FLOAT: 
                object.add(tag.getFloat(i));
                break;
                case NbtDataType.TYPE_DOUBLE: 
                object.add(tag.getDouble(i));
                break;
                case NbtDataType.TYPE_STRING: 
                object.add(tag.getString(i));
                break;
                case NbtDataType.TYPE_LIST: 
                object.add(getArray(tag.getListTag(i)));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                object.add(getMapTag(tag.getCompoundTag(i)));
                break;
            }
            object.add(tag.getValueType(i));
            array.add(object);
        }
        return array;
    }
    public static JsonArray getObject(String key, NativeCompoundTag tag){
        JsonArray object = new JsonArray();
        int type = tag.getValueType(key);
        switch (type) {
            case NbtDataType.TYPE_BYTE:
                object.add(tag.getByte(key));
            break;
            case NbtDataType.TYPE_SHORT: 
                object.add(tag.getShort(key));
            break;
            case NbtDataType.TYPE_INT: 
                object.add(tag.getInt(key));
            break;
            case NbtDataType.TYPE_INT64: 
                object.add(tag.getInt64(key));
            break;
            case NbtDataType.TYPE_FLOAT: 
                object.add(tag.getFloat(key));
            break;
            case NbtDataType.TYPE_DOUBLE: 
                object.add(tag.getDouble(key));
            break;
            case NbtDataType.TYPE_STRING: 
                object.add(tag.getString(key));
            break;
            case NbtDataType.TYPE_LIST: 
                object.add(getArray(tag.getListTag(key)));
            break;
            case NbtDataType.TYPE_COMPOUND: 
                object.add(getMapTag(tag));
            break;
        }
        object.add(type);
        return object;
    }

    public static JsonObject getMapTag(NativeCompoundTag tag){
        JsonObject object = new JsonObject();
        String[] keys = tag.getAllKeys();
        for(String key : keys)
            object.add(key, getObject(key, tag));
        return object;
    }

    public static void putValue(NativeCompoundTag tag, ArrayList<Object> object, String key){
        int type = ((Double)object.get(1)).intValue();
        switch (type) {
            case NbtDataType.TYPE_BYTE:
                tag.putByte(key, ((Double)object.get(0)).intValue());
            break;
            case NbtDataType.TYPE_SHORT: 
                tag.putShort(key, ((Double)object.get(0)).intValue());
            break;
            case NbtDataType.TYPE_INT: 
                tag.putInt(key, ((Double)object.get(0)).intValue());
            break;
            case NbtDataType.TYPE_INT64: 
                tag.putInt64(key, ((Double)object.get(0)).intValue());
            break;
            case NbtDataType.TYPE_FLOAT: 
                tag.putFloat(key, ((Double)object.get(0)).floatValue());
            break;
            case NbtDataType.TYPE_DOUBLE: 
                tag.putDouble(key, ((Double)object.get(0)).doubleValue());
            break;
            case NbtDataType.TYPE_STRING: 
                tag.putString(key, (String)object.get(0));
            break;
            case NbtDataType.TYPE_LIST: 
                tag.putListTag(key, parse((ArrayList<ArrayList<Object>>) object.get(0)));
            break;
            case NbtDataType.TYPE_COMPOUND: 
                tag.putCompoundTag(key, parse((LinkedTreeMap<String, ArrayList<Object>>) object.get(0)));
            break;
        }
    }

    public static NativeListTag parse(ArrayList<ArrayList<Object>> array){
        NativeListTag list = new NativeListTag();
        for (int i = 0;i < array.size();i++) {
            ArrayList<Object> object = array.get(i);
            int type = ((Double)object.get(1)).intValue();
            switch (type) {
                case NbtDataType.TYPE_BYTE:
                    list.putByte(i, ((Double)object.get(0)).intValue());
                break;
                case NbtDataType.TYPE_SHORT: 
                    list.putShort(i, ((Double)object.get(0)).intValue());
                break;
                case NbtDataType.TYPE_INT: 
                    list.putInt(i, ((Double)object.get(0)).intValue());
                break;
                case NbtDataType.TYPE_INT64: 
                    list.putInt64(i, ((Double)object.get(0)).intValue());
                break;
                case NbtDataType.TYPE_FLOAT: 
                    list.putFloat(i, ((Double)object.get(0)).floatValue());
                break;
                case NbtDataType.TYPE_DOUBLE: 
                    list.putDouble(i, ((Double)object.get(0)).doubleValue());
                break;
                case NbtDataType.TYPE_STRING: 
                    list.putString(i, (String)object.get(0));
                break;
                case NbtDataType.TYPE_LIST: 
                    list.putListTag(i, parse((ArrayList<ArrayList<Object>>) object.get(0)));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                    list.putCompoundTag(i, parse((LinkedTreeMap<String, ArrayList<Object>>) object.get(0)));
                break;
            }
        }
        return list;
    }
    public static NativeCompoundTag parse(LinkedTreeMap<String, ArrayList<Object>> object){
        NativeCompoundTag tag = new NativeCompoundTag();
        object.forEach(new BiConsumer<String,ArrayList<Object>>() {
            @Override
            public void accept(String t, ArrayList<Object> u) {
                putValue(tag, u, t);
            }
        });
        return tag;
    }
}
