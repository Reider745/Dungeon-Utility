package com.reider.dungeonutility;

import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.reider.dungeonutility.multiversions.notwrap.PackNotWrap;
import com.reider.dungeonutility.multiversions.wrap.PackWrap;
import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;
import com.zhekasmirnov.innercore.api.mod.ui.window.UIWindow;
import org.mozilla.javascript.Wrapper;

import java.util.HashMap;
import java.util.Random;

public class DungeonUtilityMain {
    private static IPackVersion packVersion;
    public static final String logger_name = "DungeonUtility";

    static {
        try {
            Class.forName("com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap");
            packVersion = new PackWrap();
        } catch (Exception ignore) {
            // Дополнительный слой проверки
            try {
                new UIWindow(ScriptableObjectHelper.createEmpty());
                packVersion = new PackWrap();
            } catch (Throwable e) {
                packVersion = new PackNotWrap();
            }
        }

        Logger.info(logger_name, "end load..., Compatibility mode: " + packVersion.getClass());
    }

    public static void boot(HashMap<?, ?> args){
        IJsFunctionImpl func = _args -> {
            Object random = _args[2];
            try{
                if(_args[2] instanceof Wrapper)
                    random = ((Wrapper) random).unwrap();
            }catch (Throwable ignore){}


            StructurePieceController.getPiece().generation(
                    ((Number) _args[0]).intValue(),
                    ((Number) _args[1]).intValue(),
                    (Random) random,
                    ((Number) _args[3]).intValue()
            );
            return null;
        };

        packVersion.addCallback("GenerateChunk", func);
        packVersion.addCallback("GenerateCustomDimensionChunk", func);

        /*if(!AdaptedScriptAPI.isDedicatedServer()){
            packVersion.addCallback("PostProcessChunk", _args -> {
                StructurePieceController.getPiece().generationPost(
                        ((Number) _args[0]).intValue(),
                        ((Number) _args[1]).intValue(),
                        NativeBlockSource.getCurrentWorldGenRegion()
                );
                return null;
            });
        }*/

        Logger.info(logger_name, "Added callback generation");
    }

    public static IPackVersion getPackVersionApi(){
        return packVersion;
    }
}
