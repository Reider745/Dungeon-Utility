package com.reider.dungeonutility.command;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.formats.LoaderType;
import com.reider.dungeonutility.struct.formats.legacy.BlockPalette;
import com.reider.dungeonutility.struct.loaders.StructureLoader;
import com.reider.dungeonutility.struct.loaders.StructurePool;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.apparatus.mcpe.NativePlayer;
import com.zhekasmirnov.apparatus.multiplayer.Network;
import com.zhekasmirnov.innercore.api.NativeAPI;

public class SetClientStructure {
    static {
        Network.getSingleton().addServerPacket("du.set_client_structure", (client, data, meta, type) -> {
            final long playerUid= client.getPlayerUid();
            final NativePlayer player = new NativePlayer(playerUid);

            if(player.isOperator() && data instanceof byte[]) {
                final LoaderType loaderType = LoaderType.getType("DU2");
                final float[] pos = new float[3];
                NativeAPI.getPosition(playerUid, pos);

                if(loaderType != null) {
                    long time = System.currentTimeMillis();
                    StructureDescription description =  loaderType.read((byte[]) data, null, BlockPalette.DEFAULT);

                    NativeAPI.clientMessage("Structure network read: " + (System.currentTimeMillis() - time) + "ms");
                    time = System.currentTimeMillis();

                    description.set((int) pos[0], (int) pos[1], (int) pos[2], NativeBlockSource.getDefaultForActor(playerUid));

                    NativeAPI.clientMessage("Structure set: " + (System.currentTimeMillis() - time) + "ms, blocks: " + description.blocks.length);
                }
            }
        });
    }

    public static void processCommand(String cmd) {
        final String[] args = cmd.split(" ");
        if(args.length >= 4 && args[0].equals("/struct") && args[1].equals("setworld")) {
            final String poolName = args[2];
            final String name = args[3];

            final StructurePool pool = StructureLoader.getStructurePool(poolName);
            if(pool != null) {
                final StructureDescription structure = pool.getStructure(name);
                if(structure != null) {
                    final LoaderType loaderType = LoaderType.getType("DU2");
                    if (loaderType != null)
                        Network.getSingleton().getClient().send("du.set_client_structure", loaderType.save(structure));
                    NativeAPI.preventDefault();
                }
            }
        }
    }
}
