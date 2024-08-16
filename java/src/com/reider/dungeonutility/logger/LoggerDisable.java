package com.reider.dungeonutility.logger;

public class LoggerDisable implements ILogger {
    @Override
    public void debug(String text) {}

    @Override
    public void open() {}

    @Override
    public void close() {}

    @Override
    public void updateСhart(String key, String title, int value) {}

    @Override
    public void error(Exception e) {}

    @Override
    public boolean canEnable(String key) {return true;}

    @Override
    public void setEnable(String key, boolean enable) {}

    @Override
    public void setAdditionSetting(Object setting) {}
}
