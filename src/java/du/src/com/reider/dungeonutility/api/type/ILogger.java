package com.reider.dungeonutility.api.type;

import org.mozilla.javascript.ScriptableObject;

public interface ILogger {
    void debug(String text);
    void open();
    void close();
    void updateDebug(String key, String text, boolean force);
    void updateDebug(String key, String text);
    void updateСhart(String key, String title, int value);
    void error(Exception e);
    boolean canEnable(String key);
    void setEnable(String key, boolean enable);
    void setAdditionSetting(ScriptableObject setting);
}
