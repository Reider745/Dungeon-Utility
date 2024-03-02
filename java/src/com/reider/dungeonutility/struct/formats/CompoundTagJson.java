package com.reider.dungeonutility.struct.formats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;
import com.zhekasmirnov.innercore.api.nbt.NativeListTag;
import com.zhekasmirnov.innercore.api.nbt.NbtDataType;

public class CompoundTagJson {
    public static JSONArray getArray(NativeListTag tag) throws JSONException{
        JSONArray array = new JSONArray();
        for (int i = 0; i < tag.length(); i++) {
            JSONArray object = new JSONArray();
            switch (tag.getValueType(i)) {
                case NbtDataType.TYPE_BYTE:
                    object.put(tag.getByte(i));
                break;
                case NbtDataType.TYPE_SHORT: 
                    object.put(tag.getShort(i));
                break;
                case NbtDataType.TYPE_INT: 
                    object.put(tag.getInt(i));
                break;
                case NbtDataType.TYPE_INT64: 
                    object.put(tag.getInt64(i));
                break;
                case NbtDataType.TYPE_FLOAT: 
                    object.put(tag.getFloat(i));
                break;
                case NbtDataType.TYPE_DOUBLE: 
                    object.put(tag.getDouble(i));
                break;
                case NbtDataType.TYPE_STRING: 
                    object.put(tag.getString(i));
                break;
                case NbtDataType.TYPE_LIST: 
                    object.put(getArray(tag.getListTag(i)));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                    object.put(getMapTag(tag.getCompoundTag(i)));
                break;
            }
            object.put(tag.getValueType(i));
            array.put(object);
        }
        return array;
    }
    public static JSONArray getObject(String key, NativeCompoundTag tag) throws JSONException{
        JSONArray object = new JSONArray();
        int type = tag.getValueType(key);
        switch (type) {
            case NbtDataType.TYPE_BYTE:
                object.put(tag.getByte(key));
            break;
            case NbtDataType.TYPE_SHORT: 
                object.put(tag.getShort(key));
            break;
            case NbtDataType.TYPE_INT: 
                object.put(tag.getInt(key));
            break;
            case NbtDataType.TYPE_INT64: 
                object.put(tag.getInt64(key));
            break;
            case NbtDataType.TYPE_FLOAT: 
                object.put(tag.getFloat(key));
            break;
            case NbtDataType.TYPE_DOUBLE: 
                object.put(tag.getDouble(key));
            break;
            case NbtDataType.TYPE_STRING: 
                object.put(tag.getString(key));
            break;
            case NbtDataType.TYPE_LIST: 
                object.put(getArray(tag.getListTag(key)));
            break;
            case NbtDataType.TYPE_COMPOUND: 
                object.put(getMapTag(tag));
            break;
        }
        object.put(type);
        return object;
    }

    public static JSONObject getMapTag(NativeCompoundTag tag) throws JSONException{
        JSONObject object = new JSONObject();
        String[] keys = tag.getAllKeys();
        for(String key : keys)
            object.put(key, getObject(key, tag));
        return object;
    }

    public static void putValue(NativeCompoundTag tag, JSONArray object, String key) throws JSONException{
        int type = object.getInt(1);
        switch (type) {
            case NbtDataType.TYPE_BYTE:
                tag.putByte(key, object.getInt(0));
            break;
            case NbtDataType.TYPE_SHORT: 
                tag.putShort(key, object.getInt(0));
            break;
            case NbtDataType.TYPE_INT: 
                tag.putInt(key, object.getInt(0));
            break;
            case NbtDataType.TYPE_INT64: 
                tag.putInt64(key, object.getInt(0));
            break;
            case NbtDataType.TYPE_FLOAT: 
                tag.putFloat(key, (float) object.getDouble(0));
            break;
            case NbtDataType.TYPE_DOUBLE: 
                tag.putDouble(key, object.getDouble(0));
            break;
            case NbtDataType.TYPE_STRING: 
                tag.putString(key, object.getString(0));
            break;
            case NbtDataType.TYPE_LIST: 
                tag.putListTag(key, parse(object.getJSONArray(0)));
            break;
            case NbtDataType.TYPE_COMPOUND: 
                tag.putCompoundTag(key, parse(object.getJSONObject(0)));
            break;
        }
    }

    public static NativeListTag parse(JSONArray array) throws JSONException{
        NativeListTag list = new NativeListTag();
        for (int i = 0;i < array.length();i++) {
            JSONArray object = array.getJSONArray(i);
            int type = object.getInt(1);
            switch (type) {
                case NbtDataType.TYPE_BYTE:
                    list.putByte(i, object.getInt(0));
                break;
                case NbtDataType.TYPE_SHORT: 
                    list.putShort(i, object.getInt(0));
                break;
                case NbtDataType.TYPE_INT: 
                    list.putInt(i, object.getInt(0));
                break;
                case NbtDataType.TYPE_INT64: 
                    list.putInt64(i, object.getInt(0));
                break;
                case NbtDataType.TYPE_FLOAT: 
                    list.putFloat(i, (float) object.getDouble(0));
                break;
                case NbtDataType.TYPE_DOUBLE: 
                    list.putDouble(i, object.getDouble(0));
                break;
                case NbtDataType.TYPE_STRING: 
                    list.putString(i, (String)object.get(0));
                break;
                case NbtDataType.TYPE_LIST: 
                    list.putListTag(i, parse(object.getJSONArray(0)));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                    list.putCompoundTag(i, parse(object.getJSONObject(0)));
                break;
            }
        }
        return list;
    }
    public static NativeCompoundTag parse(JSONObject object) throws JSONException{
        NativeCompoundTag tag = new NativeCompoundTag();
        Iterator<String> it = object.keys();
        while (it.hasNext()) {
            String key = it.next();
            putValue(tag, object.getJSONArray(key), key);
        }
        return tag;
    }
}
