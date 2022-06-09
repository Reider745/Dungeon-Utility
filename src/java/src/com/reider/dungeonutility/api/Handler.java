package com.reider.dungeonutility.api;

import java.util.ArrayList;

public class Handler {
    public Handler(){
        register(this);
    }

    public void apiLoad(){

    }

    private static ArrayList<Handler> handlers = new ArrayList<Handler>();
    private static void register(Handler handler){
        handlers.add(handler);
    }
    public static ArrayList<Handler> getAllHandler(){
        return handlers;
    }
}
