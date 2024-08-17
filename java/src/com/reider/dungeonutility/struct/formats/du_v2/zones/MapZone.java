package com.reider.dungeonutility.struct.formats.du_v2.zones;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public abstract class MapZone extends BaseZone {
    private static final byte TYPE_EMPTY = -1;
    private static final byte TYPE_BYTE = 0;
    private static final byte TYPE_SHORT = 1;
    private static final byte TYPE_INT = 2;
    private static final byte TYPE_STRING = 3;
    private static final byte TYPE_BOOLEAN = 4;

    private static final byte TRUE_BYTE = 1;
    private static final byte FALSE_BYTE = 0;

    private final HashMap<Byte, Map.Entry<Byte, Object>> map = new HashMap<>();

    public final Byte getType(byte key){
        final Map.Entry<Byte, Object> value = map.get(key);

        if(value != null)
            return value.getKey();

        return TYPE_EMPTY;
    }

    public final byte getByte(byte key){
        final Map.Entry<Byte, Object> value = map.get(key);

        if(value != null && value.getKey().equals(TYPE_BYTE))
            return (byte) value.getValue();

        return 0;
    }

    public final boolean getBoolean(byte key){
        return getByte(key) == TRUE_BYTE;
    }

    public final short getShort(byte key){
        final Map.Entry<Byte, Object> value = map.get(key);

        if(value != null && value.getKey().equals(TYPE_SHORT))
            return (short) value.getValue();

        return 0;
    }

    public final int getInt(byte key){
        final Map.Entry<Byte, Object> value = map.get(key);

        if(value != null && value.getKey().equals(TYPE_INT))
            return (int) value.getValue();

        return 0;
    }

    public final String getString(byte key){
        final Map.Entry<Byte, Object> value = map.get(key);

        if(value != null && value.getKey().equals(TYPE_INT))
            return (String) value.getValue();

        return "0";
    }



    public final void put(byte key, byte value){
        map.put(key, new AbstractMap.SimpleEntry<>(TYPE_BYTE, value));
    }

    public final void put(byte key, boolean value){
        put(key, value ? TRUE_BYTE : FALSE_BYTE);
    }

    public final void put(byte key, short value){
        map.put(key, new AbstractMap.SimpleEntry<>(TYPE_SHORT, value));
    }

    public final void put(byte key, int value){
        map.put(key, new AbstractMap.SimpleEntry<>(TYPE_INT, value));
    }

    public final void put(byte key, String value){
        map.put(key, new AbstractMap.SimpleEntry<>(TYPE_STRING, value));
    }

    @Override
    public void read(ByteBuffer buffer) {
        final byte count = buffer.get();

        for(byte i = 0;i < count;i++){
            final byte type = buffer.get();
            final byte key = buffer.get();

            switch (type){
                case TYPE_BOOLEAN:
                case TYPE_BYTE:
                    map.put(key, new AbstractMap.SimpleEntry<>(type, buffer.get()));
                    break;
                case TYPE_SHORT:
                    map.put(key, new AbstractMap.SimpleEntry<>(type, buffer.getShort()));
                    break;
                case TYPE_INT:
                    map.put(key, new AbstractMap.SimpleEntry<>(type, buffer.getInt()));
                    break;
                case TYPE_STRING:
                    final short length = buffer.getShort();
                    final byte[] bytes = new byte[length];
                    for(int a = 0;a < length;a++)
                        bytes[a] = buffer.get();
                    map.put(key, new AbstractMap.SimpleEntry<>(type, new String(bytes)));
                    break;
            }
        }
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.put((byte) map.size());

        for(Byte key : map.keySet()){
            final Map.Entry<Byte, Object> value = map.get(key);
            buffer.put(value.getKey());
            buffer.put(key);


            switch (value.getKey()){
                case TYPE_BOOLEAN:
                case TYPE_BYTE:
                    buffer.put((byte) value.getValue());
                    break;
                case TYPE_SHORT:
                    buffer.putShort((short) value.getValue());
                    break;
                case TYPE_INT:
                    buffer.putInt((int) value.getValue());
                    break;
                case TYPE_STRING:
                    final String str = (String) value.getValue();
                    buffer.putShort((short) str.length());
                    buffer.put(str.getBytes());
                    break;
            }
        }
    }

    @Override
    public int mathLength() {
        int length = 1;

        for(Byte key : map.keySet()){
            final Map.Entry<Byte, Object> value = map.get(key);
            length += 2;


            switch (value.getKey()){
                case TYPE_BOOLEAN:
                case TYPE_BYTE:
                    length += 1;
                    break;
                case TYPE_SHORT:
                    length += 2;
                    break;
                case TYPE_INT:
                    length += 4;
                    break;
                case TYPE_STRING:
                    length += 2;
                    length += ((String) value.getValue()).getBytes().length;
                    break;
            }
        }

        return length;
    }

    public String toName(byte id){
        return String.valueOf(id);
    }

    public String toType(byte id){
        switch (id){
            case TYPE_EMPTY:
                return "empty";
            case TYPE_BYTE:
                return "byte";
            case TYPE_SHORT:
                return "short";
            case TYPE_BOOLEAN:
                return "boolean";
            case TYPE_INT:
                return "int";
            case TYPE_STRING:
                return "string";
        }
        return String.valueOf(id);
    }

    public String toTextValue(byte type, Object value){
        if(type == TYPE_BOOLEAN)
            return String.valueOf(((Byte) value) == 1);
        return value.toString();
    }

    @Override
    public String toString() {
        String content = super.toString();
        for(Byte key : map.keySet()) {
            final Map.Entry<Byte, Object> value = map.get(key);
            content += toName(key)+" = "+toTextValue(value.getKey(), value.getValue())+" <"+toType(value.getKey())+">\n";
        }
        content += "\n\n";
        return content;
    }
}
