package com.reider.dungeonutility.logger;

import java.util.ArrayList;
import java.util.HashMap;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.logger.ILogger;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindowLocation;

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

        private final UIWindow window;

        public ThreadWindowUpdate(UIWindow window){
            this.window = window;
        }

        @Override
        public void run() {
            while(active){
                try {
                    synchronized (window) {
                        window.forceRefresh();
                    }
                    sleep(time);
                } catch (Exception e) {}
            }
        }
    }

    public int TEXT_SIZE = 15;
    public static int X = 0;
    public static int GREEN = Color.argb(.5f, .0f, 1f, .0f);

    private final UIWindow window = new UIWindow(new UIWindowLocation());


    private HashMap<String, Integer> postions = new HashMap<>();
    private int new_y = 0;
    private int STORAGE = 30;
    private HashMap<String, ArrayList<Integer>> charts = new HashMap<>();
    private int CHART = 45;
    private int WIDTH_CHART = 100;
    private final HashMap<String, Boolean> enables = new HashMap<>();

    @Override
    public void setAdditionSetting(Object setting) {
        final IJsObject scriptObjectWrap = DungeonUtilityMain.getPackVersionApi().createObject(setting);

        TEXT_SIZE = scriptObjectWrap.getInt("text_size", TEXT_SIZE);
        STORAGE = scriptObjectWrap.getInt("chart_info", STORAGE);
        CHART = scriptObjectWrap.getInt("chart_height", CHART);
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
    public void setEnable(String key, boolean enable) {
        synchronized(enables){
            enables.put(key, enable);
        }
    }

    public LoggerEnable(){
        window.setAsGameOverlay(true);
        window.setTouchable(false);
        window.setDynamic(false);
        window.setBackgroundColor(Color.argb(0, 0, 0, 0));

        final IPackVersion version = DungeonUtilityMain.getPackVersionApi();

        IJsObject content = version.createObjectEmpty();

        content.setScriptObj("drawing", version.createArrayEmpty());
        content.setScriptObj("elements", version.createObjectEmpty());

        version.setContent(window, content);
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
        synchronized (window) {
            window.open();
        }
    }

    @Override
    public void close() {
        synchronized (window) {
            window.close();
        }
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

    private void updateDebug(String key, String text) {
        if(!canEnable(key)) return;

        final IPackVersion version = DungeonUtilityMain.getPackVersionApi();
        IJsObject text_element = version.createObjectEmpty();
        IJsObject font = version.createObjectEmpty();
        IJsObject content = version.getContent(window);

        font.setInt("size", TEXT_SIZE);
        font.setInt("color", GREEN);

        text_element.setString("type", "text");
        text_element.setString("text", text);
        text_element.setInt("x", X);
        text_element.setInt("y", getY(key, TEXT_SIZE));
        text_element.setScriptObj("font", font);

        IJsObject elements = (IJsObject) content.getScriptObj("elements");
        elements.setScriptObj(key, text_element);
        content.setScriptObj("elements", elements);
        version.setContent(window, content);
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

        final ChartInfo info = getChartInfo(key, value);
        final int y = getY(key+"_chart", CHART);
        final int width = WIDTH_CHART/STORAGE;

        synchronized(window){
            updateDebug(key+"_title", title);
            updateDebug(key+"_info", "min: "+info.min+", max: "+ info.max + ", avg: "+info.avg);

            final IPackVersion version = DungeonUtilityMain.getPackVersionApi();

            IJsObject content = version.getContent(window);
            IJsObject elements = (IJsObject) content.getScriptObj("elements");

            IJsObject element = version.createObjectEmpty();
            element.setString("type", "custom");

            IJsObject custom = version.createObjectEmpty();
            element.setScriptObj("onDraw", version.createJavaFunction((args) -> {
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
            }));

            element.setScriptObj("custom", custom);
            
            elements.setScriptObj(key, element);
            content.setScriptObj("elements", elements);

            version.setContent(window, content);

            window.forceRefresh();
        }
    }
}