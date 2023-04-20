package com.reider.dungeonutility.api.type;

public interface ILogger {
    void debug(String text);
    void open();
    void close();
    void updateDebug(String key, String text);
}
