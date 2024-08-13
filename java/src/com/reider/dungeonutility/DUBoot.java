package com.reider.dungeonutility;

import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.notwrap.PackNotWrap;
import com.reider.dungeonutility.multiversions.wrap.PackWrap;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;

import java.util.HashMap;

public class DUBoot {
    private static IPackVersion packVersion;

    public static void boot(HashMap<?, ?> args){
        try {
            Class.forName("com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap");
            packVersion = new PackWrap();
        }catch (Exception ignore){
            try{
                new UIWindow(ScriptableObjectHelper.createEmpty());
                packVersion = new PackNotWrap();
            }catch (Exception e){
                packVersion = new PackWrap();
            }
        }

        Logger.info(StructureLoader.logger_name, "end load..., Compatibility mode: "+packVersion.getClass());
    }

    public static IPackVersion getPackVersionApi(){
        return packVersion;
    }
}
