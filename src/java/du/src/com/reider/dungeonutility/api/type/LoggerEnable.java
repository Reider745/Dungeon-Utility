package com.reider.dungeonutility.api.type;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;

import android.graphics.Color;


public class LoggerEnable implements ILogger {
    private UIWindow window = new UIWindow(ScriptableObjectHelper.createEmpty());

    public static final int TEXT_SIZE = 20;
    public static final int X = 0;
    public static final int GREEN = Color.argb(.5f, .0f, 1f, .0f);

    public LoggerEnable(){
        window.setAsGameOverlay(true);
        window.setTouchable(false);
        window.setBackgroundColor(Color.argb(0, 0, 0, 0));

        ScriptableObject content = window.getContent();

        content.put("drawing", content, ScriptableObjectHelper.createEmptyArray());
        content.put("elements", content, ScriptableObjectHelper.createEmpty());
    }
    
    @Override
    public void debug(String text) {
        Logger.debug(text);
        NativeAPI.clientMessage(text);
    }

    @Override
    public void error(Exception e){
        Logger.error(ICLog.getStackTrace(e));
    }

    @Override
    public void open() {
        window.open();
    }

    @Override
    public void close() {
        window.close();
    }

    public HashMap<String, Integer> postions = new HashMap<>();
    public int new_y = 0;

    public int getY(String key, int size){
        Object y = postions.get(key);
        if(y == null){
            postions.put(key, new_y);
            int _y = new_y;
            new_y += size;
            return _y;
        }
        return (Integer) y;
    }

    @Override
    public void updateDebug(String key, String text, boolean force) {
        synchronized(window){
            ScriptableObject text_element = ScriptableObjectHelper.createEmpty();
            ScriptableObject font = ScriptableObjectHelper.createEmpty();
            ScriptableObject content = window.getContent();

            font.put("size", font, TEXT_SIZE);
            font.put("color", font, GREEN);

            text_element.put("type", text_element, "text");
            text_element.put("text", text_element, text);
            text_element.put("x", text_element, X);
            text_element.put("y", text_element, getY(key, TEXT_SIZE));
            text_element.put("font", text_element, font);
            ScriptableObject elements = (ScriptableObject) content.get("elements");
            elements.put(key, elements, text_element);
            content.put("elements", content, elements);
            window.setContent(content);

            if(force)
                window.forceRefresh();
        }
    }

    public final int STORAGE = 30;

    public HashMap<String, ArrayList<Integer>> charts = new HashMap<>();

    public static class ChartInfo {
        public ArrayList<Integer> values;
        public int min = -1;
        public int max = -1;
        public int avg = -1;
    }

    public ArrayList<Integer> getChartValues(String key){
        ArrayList<Integer> chart = charts.get(key);
        if(chart == null){
            chart = new ArrayList<>();
            for(int i = 0;i < STORAGE;i++)
                chart.add(0);
            charts.put(key, chart);
        }
        return chart;
    }

    public ChartInfo getChartInfo(String key, int added){
        ArrayList<Integer> charts = getChartValues(key);

        charts.remove(0);
        charts.add(added);

        ChartInfo info = new ChartInfo();
        info.min = charts.get(0);
        int sum = 0;
        for(Integer value : charts){
            sum += value;
            info.min = Math.min(info.min, value);
            info.max = Math.max(info.max, value);
        }
        info.values = charts;
        info.avg = sum / STORAGE;
        return info;
    }

    public HashMap<String, Integer> cache_chart = new HashMap<>();

    public int getChartId(String key, int index){
        Integer id = cache_chart.get(key);
        if(id == null){
            cache_chart.put(key, index);
            return index;
        }
        return id;
    }

    public static final int CHART = 50;

    @Override
    public void updateСhart(String key, String title, int value) {
        
        ChartInfo info = getChartInfo(key, value);
        updateDebug(key+"_title", title, false);
        updateDebug(key+"_info", "min: "+info.min+", max: "+ info.max + ", avg: "+info.avg, false);

        int y = getY(key+"_chart", CHART);
        int width = 100/STORAGE;

        synchronized(window){
            
            ScriptableObject content = window.getContent();
            NativeArray drawing = (NativeArray) content.get("drawing");

            for(int i = 0;i < STORAGE;i++){
                ScriptableObject line = ScriptableObjectHelper.createEmpty();
                line.put("type", line, "line");
                line.put("x1", line, X+width*i);
                line.put("y1", line, y+((info.values.get(i)+.00000000000000001) / info.max)*CHART);
                line.put("x2", line, X+width*i);
                line.put("y2", line, y);
                line.put("width", line, width);
                line.put("color", line, GREEN);

                drawing.put(getChartId(key+":"+i, drawing.size()), drawing, line);
            }
            
            window.setContent(content);
            window.forceRefresh();
        }
    }

    @Override
    public void updateDebug(String key, String text) {
        updateDebug(key, text, true);
    }
}