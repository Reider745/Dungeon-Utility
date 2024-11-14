package com.reider.dungeonutility.api;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.unlimited.IDRegistry;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Utils {
    private static boolean isNumber(String str){
        char[] chars = str.toCharArray();
        for(char c : chars)
            if(!Character.isDigit(c))
                return false;
        return true;
    }

    public static int getIdBlock(String id){
        if(id.isEmpty())
            return 0;
        if(isNumber(id))
            return Integer.parseInt(id);
        return IDRegistry.genBlockID(id);
    }

    public static Object getIdBlock(int id){
        if(id > 8000)
            return IDRegistry.getNameByID(id);
        return id;
    }

    public static int getInt(String integer){
        if(integer.equals(""))
            return 0;
        return Integer.parseInt(integer);
    }

    public static String[] split(String str, char symbol){
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        char[] chars = str.toCharArray();
        for(char s : chars)
            if(s == symbol)
                list.add("");
            else{
                int index = list.size() - 1;
                list.set(index, list.get(index) + s);
            }
        return list.toArray(new String[list.size()]);
    }

    public static HashMap<String, Integer> getHashMapToJson(JSONObject object) throws JSONException {
        HashMap<String, Integer> result = new HashMap<>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();

            result.put(key, object.getInt(key));
        }
        return result;
    }

    public static JSONObject getJsonForHashMap(Map<String, Integer> object) throws JSONException{
        JSONObject result = new JSONObject();
        Iterator<String> keys = object.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();

            result.put(key, object.get(key));
        }
        return result;
    }

    public static byte[] readFileBytes(String path){
        try {
            final File file = new File(path);
            final FileInputStream inputStream = new FileInputStream(file);
            final byte[] bytes = new byte[(int) file.length()];

            inputStream.read(bytes);
            inputStream.close();

            return bytes;
        } catch (Exception e) {
            Logger.warning("Read "+ICLog.getStackTrace(e));
        }
        return new byte[]{};
    }

    public static void writeFileBytes(String path, byte[] bytes){
        try{
            final File file = new File(path);
            if(!file.exists())
                file.createNewFile();

            final FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
        }catch (IOException e){
            Logger.debug(DungeonUtilityMain.logger_name, ICLog.getStackTrace(e));
        }
    }

    //Максимальный размер строки 65536 символов
    private static final int LIMIT_STRING = -Short.MIN_VALUE + Short.MAX_VALUE + 1;

    public static void putString(ByteBuffer buffer, String str){
        buffer.putShort((short) (Math.min(str.length(), LIMIT_STRING) + Short.MIN_VALUE));
        final byte[] bytes = str.getBytes();

        if(str.length() >= LIMIT_STRING)
            for(int i = 0;i < LIMIT_STRING;i++)
                buffer.put(bytes[i]);
        else
            buffer.put(str.getBytes());
    }

    public static String readString(ByteBuffer buffer){
        final byte[] bytes = new byte[buffer.getShort() - Short.MIN_VALUE];
        for(int a = 0;a < bytes.length;a++)
            bytes[a] = buffer.get();
        return new String(bytes);
    }

    public static int mathLength(String str){
        return 2 + Math.min(str.getBytes().length, LIMIT_STRING);
    }
}
