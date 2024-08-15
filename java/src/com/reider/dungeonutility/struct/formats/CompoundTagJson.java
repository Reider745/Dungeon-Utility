package com.reider.dungeonutility.struct.formats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;
import com.zhekasmirnov.innercore.api.nbt.NativeListTag;
import com.zhekasmirnov.innercore.api.nbt.NbtDataType;

public class CompoundTagJson {
    // Сохранение в json
    public static Object getArray(NativeListTag tag, HashMap<Long, Object> hash, AtomicReference<Integer> id) throws JSONException{
        if(hash.containsKey(tag.pointer)){
            return hash.get(tag.pointer);
        }

        final JSONArray array = new JSONArray();
        id.set(id.get()+1);

        final JSONArray hash_array = new JSONArray();
        hash_array.put(null);
        hash_array.put(id.get());
        hash.put(tag.pointer, hash_array);

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
                    object.put(getArray(tag.getListTag(i), hash, id));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                    object.put(getMapTag(tag.getCompoundTag(i), hash, id));
                break;
            }
            object.put(tag.getValueType(i));

            array.put(object);
            array.put(id.get());
        }
        return array;
    }
    public static JSONArray getObject(String key, NativeCompoundTag tag, HashMap<Long, Object> hash, AtomicReference<Integer> id) throws JSONException {
        JSONArray object = new JSONArray();

        int type = tag.getValueType(key);
        boolean addedId = false;
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
                object.put(getArray(tag.getListTag(key), hash, id));
                addedId = true;
            break;
            case NbtDataType.TYPE_COMPOUND: 
                object.put(getMapTag(tag.getCompoundTag(key), hash, id));
                addedId = true;
            break;
        }
        object.put(type);
        if(addedId)
            object.put(id.get());
        return object;
    }

    public static Object getMapTag(NativeCompoundTag tag, HashMap<Long, Object> hash, AtomicReference<Integer> id) throws JSONException{
        if(hash.containsKey(tag.pointer)){
            return hash.get(tag.pointer);
        }

        final JSONObject object = new JSONObject();
        id.set(id.get()+1);

        final JSONArray hash_array = new JSONArray();
        hash_array.put(null);
        hash_array.put(id.get());
        hash.put(tag.pointer, hash_array);

        final String[] keys = tag.getAllKeys();
        for(String key : keys)
            object.put(key, getObject(key, tag, hash, id));
        return object;
    }


    // Чтение из json
    public static void putValue(NativeCompoundTag tag, JSONArray object, String key, HashMap<Integer, Object> hash) throws JSONException{
        final int type = object.getInt(1);
        if(object.isNull(0)){
            final Object res = hash.get(object.getInt(1));
            switch (type) {
                case NbtDataType.TYPE_LIST:
                    tag.putListTag(key, (NativeListTag) res);
                    break;
                case NbtDataType.TYPE_COMPOUND:
                    tag.putCompoundTag(key, (NativeCompoundTag) res);
                    break;
            }
            return;
        }


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
                tag.putListTag(key, parse(object.getJSONArray(0), hash));
            break;
            case NbtDataType.TYPE_COMPOUND: 
                tag.putCompoundTag(key, parse(object.getJSONObject(0), hash, object));
            break;
        }
    }

    public static NativeListTag parse(JSONArray array, HashMap<Integer, Object> hash) throws JSONException{
        final NativeListTag list = new NativeListTag();
        if(array.length() == 3){
            hash.put(array.getInt(2), list);
        }

        for (int i = 0;i < array.length();i++) {
            final Object _object = array.get(i);
            if(_object instanceof Number) continue;
            final JSONArray object = (JSONArray) _object;
            final int type = object.getInt(1);

            if(object.isNull(0)){
                final Object res = hash.get(object.getInt(1));
                switch (type) {
                    case NbtDataType.TYPE_LIST:
                        list.putListTag(i, (NativeListTag) res);
                        break;
                    case NbtDataType.TYPE_COMPOUND:
                        list.putCompoundTag(i, (NativeCompoundTag) res);
                        break;
                }
                continue;
            }


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
                    list.putListTag(i, parse(object.getJSONArray(0), hash));
                break;
                case NbtDataType.TYPE_COMPOUND: 
                    list.putCompoundTag(i, parse(object.getJSONObject(0), hash, object));
                break;
            }
        }
        return list;
    }
    public static NativeCompoundTag parse(JSONObject object, HashMap<Integer, Object> hash, JSONArray parent) throws JSONException{
        final NativeCompoundTag tag = new NativeCompoundTag();

        if(parent != null && parent.length() == 3){
            hash.put(parent.getInt(2), tag);
        }

        Iterator<String> it = object.keys();
        while (it.hasNext()) {
            String key = it.next();
            putValue(tag, object.getJSONArray(key), key, hash);
        }
        return tag;
    }
}
