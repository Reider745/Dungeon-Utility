package com.reider.dungeonutility;

import com.reider.dungeonutility.api.Handler;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.util.HashMap;

public class LibLoad extends Handler {
    public LibLoad(){
        super();
    }

    @Override
    public void apiLoad() {
    }

    public static void load(HashMap data){
        new LibLoad();
        AdaptedScriptAPI.Logger.debug("Dungeon utility java", "Loading");
    }
}
