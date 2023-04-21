package com.reider.dungeonutility.api.type;

public interface ILogger {
    void debug(String text);
    void open();
    void close();
    void updateDebug(String key, String text, boolean force);
    void updateDebug(String key, String text);
    void updateСhart(String key, String title, int value);
    void error(Exception e);
}
