package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.util.ArrayList;

public class DungeonAPI implements LoaderTypeInterface {
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList();

        String[] strings = file.split(":");
        for(String str : strings){
            String[] data = str.split("\\.");
            blocks.add(BlockData.createData(
                    Integer.parseInt(data[2]),
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    new BlockState(StructureLoader.getIdBlock(data[0]), Integer.parseInt(data[1]))
            ));
        }

        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public String save(StructureDescription stru) {
        String str = "";
        for(int i = 0;i < stru.blocks.length;i++){
            BlockData data = stru.blocks[i].getData();
            str += StructureLoader.getIdBlock(data.state.id) + "." + data.state.data + "." + data.x + "." + data.y + "." + data.z;
            if(i != stru.blocks.length - 1)
                str+=":";
        }
        return str;
    }
}
