package com.reider.dungeonutility.api.type;

import org.mozilla.javascript.ScriptableObject;

import java.util.HashMap;

import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import com.zhekasmirnov.innercore.api.mod.ui.types.WindowContentAdapter;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;

import android.graphics.Color;


public class LoggerEnable implements ILogger {
    private UIWindow window = new UIWindow(ScriptableObjectHelper.createEmpty());

    public static final int TEXT_SIZE = 20;

    public LoggerEnable(){
        window.setAsGameOverlay(true);
        window.setTouchable(false);
        window.setBackgroundColor(Color.argb(0, 0, 0, 0));

        ScriptableObject content = window.getContent();

        content.put("elements", content, ScriptableObjectHelper.createEmpty());
    }
    
    @Override
    public void debug(String text) {
        Logger.debug(text);
        NativeAPI.clientMessage(text);
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

    public int getY(String key){
        Object y = postions.get(key);
        if(y == null){
            postions.put(key, new_y);
            int _y = new_y;
            new_y += TEXT_SIZE;
            return _y;
        }
        return (Integer) y;
    }

    @Override
    public void updateDebug(String key, String text) {
        ScriptableObject text_element = ScriptableObjectHelper.createEmpty();
        ScriptableObject font = ScriptableObjectHelper.createEmpty();
        WindowContentAdapter content = new WindowContentAdapter(window.getContent());

        font.put("size", font, TEXT_SIZE);
        font.put("color", font, Color.GREEN);

        text_element.put("type", text_element, "text");
        text_element.put("text", text_element, text);
        text_element.put("x", text_element, 0);
        text_element.put("y", text_element, getY(key));
        text_element.put("font", text_element, font);
        
        content.addElement(key, text_element);

        window.setContent(content.getContent());
        window.forceRefresh();
    }
}