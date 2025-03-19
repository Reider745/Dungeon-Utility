package com.reider.dungeonutility;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.multiversions.IPackVersion;
import com.reider.dungeonutility.multiversions.js_types.IJsFunctionImpl;
import com.reider.dungeonutility.multiversions.notwrap.PackNotWrap;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.reider.dungeonutility.struct.generation.stand.LastingStand;
import com.reider.dungeonutility.struct.generation.stand.api.StandManager;
import com.reider.dungeonutility.struct.generation.stand.surface.SurfaceTowerStand;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.commontypes.Coords;
import com.zhekasmirnov.innercore.api.mod.ScriptableObjectHelper;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DungeonUtilityMain {
    private static IPackVersion packVersion;
    public static final String logger_name = "DungeonUtility";

    static {
        packVersion = new PackNotWrap();
        /*try {
            Class.forName("com.zhekasmirnov.innercore.api.scriptwrap.ScriptObjectWrap");
            packVersion = new PackWrap();
        } catch (Exception ignore) {
            // Дополнительный слой проверки
            try {
                new UIWindow(ScriptableObjectHelper.createEmpty());
                packVersion = new PackNotWrap();
            } catch (Throwable e) {
                packVersion = new PackWrap();
            }
        }*/

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

        /*packVersion.addCallback("ItemUse", _args -> {
            Coords coords = (Coords) _args[0];
            if(ScriptableObjectHelper.getIntProperty((ScriptableObject) _args[1], "id", 0) != 265)
                return null;

            final List<BlockData> blocks = new ArrayList<>();

            for(int x = -2; x < 4;x++) {
                for(int y = -2; y < 3;y++) {
                    for(int z = -2; z < 4;z++) {
                        blocks.add(BlockData.createData(x, y, z, new BlockState(y + 3, 0)));
                    }
                }
            }

            final StructureDescription description = new StructureDescription(blocks);
            final Structure structure = new Structure(description);

            final SurfaceTowerStand stand = (SurfaceTowerStand) StandManager.build(SurfaceTowerStand.ID, structure);
            stand.setBase(BlockData.createData(new BlockState(1, 0)));
            stand.setDirt(BlockData.createData(new BlockState(3, 0)), 3);
            stand.setGrass(BlockData.createData(new BlockState(2, 0)));
            structure.setStand(stand);
            structure.setStand(StandManager.build(LastingStand.ID, structure));

            structure.setStructure(
                    ScriptableObjectHelper.getIntProperty(coords, "x", 0),
                    ScriptableObjectHelper.getIntProperty(coords, "y", 0),
                    ScriptableObjectHelper.getIntProperty(coords, "z", 0),
                    NativeBlockSource.getCurrentWorldGenRegion(),
                    null
            );

            return null;
        });*/

        Logger.info(logger_name, "Added callback generation");
    }

    public static IPackVersion getPackVersionApi(){
        return packVersion;
    }
}
