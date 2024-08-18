package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.util.ArrayList;

public class DungeonAPI extends LoaderType {
    @Override
    public StructureDescription read(byte[] bytes, String path) {
        final String file = new String(bytes);
        final ArrayList<BlockData> blocks = new ArrayList<>();
        final String[] strings = file.split(":");

        for(String str : strings){
            final String[] data = str.split("\\.");
            blocks.add(BlockData.createData(
                    Integer.parseInt(data[2]),
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    StateManager.buildBlockState(Utils.getIdBlock(data[0]), Integer.parseInt(data[1]))
            ));
        }

        return new StructureDescription(blocks);
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public byte[] save(StructureDescription stru) {
        String str = "";
        for(int i = 0;i < stru.blocks.length;i++){
            final BlockData data = stru.blocks[i].getData();

            str += Utils.getIdBlock(data.state.id) + "." + data.state.data + "." + data.x + "." + data.y + "." + data.z;
            if(i != stru.blocks.length - 1)
                str+=":";
        }
        return str.getBytes();
    }
}
