package com.reider.dungeonutility.struct.formats;

import com.google.gson_du.Gson;
import com.google.gson_du.internal.LinkedTreeMap;
import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.util.ArrayList;

public class DungeonCore implements LoaderTypeInterface {
    private static char symbol = '.';
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList();
        Gson gson = new Gson();

        Object[][] json = gson.fromJson(file, Object[][].class);

        for(Object[] list : json){
            String[] datas = StructureLoader.split((String) list[1], symbol);
            BlockState state = null;
            BlockState stateExtra = null;
            if(list[0] instanceof Double)
                state = new BlockState(((Double) list[0]).intValue(), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) list[2]));
            else if(list[0] instanceof String)
                state = new BlockState(((int) (StructureLoader.BlockID.get((String) list[0], StructureLoader.BlockID))), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) list[2]));

            if(list.length >= 4) {
                Object[] extra = (Object[]) list[3];
                if(extra[0] instanceof Double)
                    stateExtra = new BlockState(((Double) extra[0]).intValue(), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) extra[1]));
                else if(extra[0] instanceof String)
                    stateExtra = new BlockState(((int) (StructureLoader.BlockID.get((String) extra[0], StructureLoader.BlockID))), StructureLoader.getHashMapToJson((LinkedTreeMap<String, Double>) extra[1]));

            }
            blocks.add(BlockData.createData(
                    Integer.parseInt(datas[1]),
                    Integer.parseInt(datas[2]),
                    Integer.parseInt(datas[3]),
                    state,
                    stateExtra
            ));
        }

        return new StructureDescription(blocks.toArray(new BlockData[blocks.size()]));
    }

    @Override
    public boolean isLoadRuntime() {
        return false;
    }

    @Override
    public String save(StructureDescription stru) {
        ArrayList<ArrayList<Object>> json = new ArrayList();

        for (BlockData bl : stru.blocks){
            BlockData data = bl.getData();
            ArrayList<Object> list = new ArrayList();
            list.add(StructureLoader.getIdBlock(data.state.id));
            list.add(data.state.data+"."+data.x+"."+ data.y+"."+ data.z);
            list.add(data.state.getNamedStates());

            if(bl.stateExtra != null){
                ArrayList<Object> extra = new ArrayList();;
                extra.add(StructureLoader.getIdBlock(data.stateExtra.id));
                extra.add(data.stateExtra.getNamedStates());
                json.add(extra);
            }

            json.add(list);
        }

        Gson gson = new Gson();
        return gson.toJson(json);
    }
}
