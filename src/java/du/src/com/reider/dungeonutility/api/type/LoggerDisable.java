package com.reider.dungeonutility.api.type;

import org.mozilla.javascript.ScriptableObject;

public class LoggerDisable implements ILogger {
    @Override
    public void debug(String text) {
        
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {
    }

    @Override
    public void updateDebug(String key, String text) {
        
    }

    @Override
    public void updateСhart(String key, String title, int value) {
        
    }

    @Override
    public void error(Exception e) {
        
    }

    @Override
    public void updateDebug(String key, String text, boolean force) {

    }

    @Override
    public boolean canEnable(String key) {
        return true;
    }

    @Override
    public void setEnable(String key, boolean enable) {

    }

    @Override
    public void setAdditionSetting(ScriptableObject setting) {
        
    }
}
