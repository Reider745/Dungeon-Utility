package com.reider.dungeonutility.api.type;

import java.util.ArrayList;
import java.util.HashMap;

import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap;
import com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap.ScriptFunctionImpl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LoggerEnable implements ILogger {
    public static class ChartInfo {
        public ArrayList<Integer> values;
        public int min = -1;
        public int max = -1;
        public int avg = -1;
    }
    public static class ThreadWindowUpdate extends Thread {
        private boolean active = true;
        private long time = 1000/15;

        private UIWindow window;

        public ThreadWindowUpdate(UIWindow window){
            this.window = window;
        }

        @Override
        public void run() {
            while(active){
                try {
                    window.forceRefresh();
                    sleep(time);
                } catch (Exception e) {}
            }
        }
    }

    public int TEXT_SIZE = 15;
    public static int X = 0;
    public static int GREEN = Color.argb(.5f, .0f, 1f, .0f);

    private UIWindow window = new UIWindow(ScriptObjectWrap.createNewEmptyObject());


    private HashMap<String, Integer> postions = new HashMap<>();
    private int new_y = 0;
    private int STORAGE = 30;
    private HashMap<String, ArrayList<Integer>> charts = new HashMap<>();
    private int CHART = 45;
    private int WIDTH_CHART = 100;
    private HashMap<String, Boolean> enables = new HashMap<>();

    @Override
    public void setAdditionSetting(Object setting) {
        ScriptObjectWrap scriptObjectWrap = ScriptObjectWrap.create(setting);

        if(scriptObjectWrap.hasKey("text_size"))
            TEXT_SIZE = scriptObjectWrap.getInt("text_size", TEXT_SIZE);
        if(scriptObjectWrap.hasKey("chart_info"))
            STORAGE = scriptObjectWrap.getInt("chart_info", STORAGE);
        if(scriptObjectWrap.hasKey("chart_height"))
            CHART = scriptObjectWrap.getInt("chart_height", CHART);
        if(scriptObjectWrap.hasKey("chart_width"))
            WIDTH_CHART = scriptObjectWrap.getInt("chart_width", WIDTH_CHART);
    }

    @Override
    public boolean canEnable(String key) {
        synchronized(enables){
            Boolean value = enables.get(key);
            if(value != null)
                return value;
            return true;
        }
    }

    @Override
    public synchronized void setEnable(String key, boolean enable) {
        synchronized(enables){
            enables.put(key, enable);
        }
    }

    public LoggerEnable(){
        window.setAsGameOverlay(true);
        window.setTouchable(false);
        window.setBackgroundColor(Color.argb(0, 0, 0, 0));

        Object content = window.getContent();

        ScriptObjectWrap obj = ScriptObjectWrap.create(content);
        obj.setScriptObj("drawing", ScriptObjectWrap.createNewEmptyArray());
        obj.setScriptObj("elements", ScriptObjectWrap.createNewEmptyObject());
    }
    
    @Override
    public void debug(String text) {
        Logger.debug(text);
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
        if(!canEnable(key)) return;
        synchronized(window){
            
            ScriptObjectWrap text_element = ScriptObjectWrap.createNewEmptyObject();
            ScriptObjectWrap font = ScriptObjectWrap.createNewEmptyObject();
            Object content = window.getContent();

            ScriptObjectWrap _content = ScriptObjectWrap.create(content);

            font.setInt("size", TEXT_SIZE);
            font.setInt("color", GREEN);

            text_element.setString("type", "text");
            text_element.setString("text", text);
            text_element.setInt("x", X);
            text_element.setInt("y", getY(key, TEXT_SIZE));
            text_element.setScriptObj("font", font);

            ScriptObjectWrap elements = _content.getScriptObj("elements");
            elements.setScriptObj(key, text_element);
            _content.setScriptObj("elements", elements);
            window.setContent(content);

            if(force)
                window.forceRefresh();
        }
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

    
    @Override
    public void updateСhart(String key, String title, int value) { 
        if(!canEnable(key)) return;
        ChartInfo info = getChartInfo(key, value);
        updateDebug(key+"_title", title, false);
        updateDebug(key+"_info", "min: "+info.min+", max: "+ info.max + ", avg: "+info.avg, false);

        int y = getY(key+"_chart", CHART);
        int width = WIDTH_CHART/STORAGE;

        synchronized(window){
            Object content = window.getContent();
            ScriptObjectWrap _content = ScriptObjectWrap.create(content);
            ScriptObjectWrap elements = _content.getScriptObj("elements");

            ScriptObjectWrap element = ScriptObjectWrap.createNewEmptyObject();
            element.setString("type", "custom");

            ScriptObjectWrap custom = ScriptObjectWrap.createNewEmptyObject();
            element.setScriptObj("onDraw", ScriptObjectWrap.createJavaFunction(new ScriptFunctionImpl() {
                @Override
                public Object call(Object[] args) {
                    Canvas canvas = (Canvas) args[1];
                    float scale = (float) args[2];
                    Paint paint = new Paint();

                    paint.setColor(GREEN);
                    paint.setStrokeWidth(width * scale);

                    for(int i = 0;i < STORAGE;i++)
                        canvas.drawLine(
                            (X+width*i) * scale,  
                            (y+CHART) * scale, 
                            (X+width*i) * scale, 
                            (float) ((y+(1-((info.values.get(i)+.00000000000000001) / info.max))*CHART) * scale), 
                            paint
                        );
                    return null;
                }
                
            }));

            element.setScriptObj("custom", custom);
            
            elements.setScriptObj(key, element);
            _content.setScriptObj("elements", elements);

            window.setContent(content);
            window.forceRefresh();
        }
    }

    @Override
    public void updateDebug(String key, String text) {
        updateDebug(key, text, true);
    }
}