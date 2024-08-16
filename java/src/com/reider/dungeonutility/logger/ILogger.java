package com.reider.dungeonutility.logger;

public interface ILogger {
    void debug(String text);
    void open();
    void close();
    void updateСhart(String key, String title, int value);
    void error(Exception e);
    boolean canEnable(String key);
    void setEnable(String key, boolean enable);
    void setAdditionSetting(Object setting);
}
