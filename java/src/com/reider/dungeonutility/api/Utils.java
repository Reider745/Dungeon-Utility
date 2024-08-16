package com.reider.dungeonutility.api;

import com.zhekasmirnov.innercore.api.unlimited.IDRegistry;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {
    private static boolean isNumber(String str){
        char[] chars = str.toCharArray();
        for(char c : chars)
            if(!Character.isDigit(c))
                return false;
        return true;
    }

    public static int getIdBlock(String id){
        if(id.equals(""))
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

    public static String getTextToFile(String path){
        String text = "";
        try {
            Reader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while((line = br.readLine())!= null)
                text+=line;
            br.close();
        } catch (Exception e) {}
        return text;
    }
}
