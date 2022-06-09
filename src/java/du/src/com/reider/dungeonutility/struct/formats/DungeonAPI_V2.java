package com.reider.dungeonutility.struct.formats;

import com.google.gson_du.stream.JsonReader;
import com.google.gson_du.stream.JsonToken;
import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.api.LoaderTypeInterface;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class DungeonAPI_V2 implements LoaderTypeInterface {
    @Override
    public StructureDescription read(String file, String path) {
        ArrayList<BlockData> blocks = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(new StringReader(file));
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()){
                JsonToken token = jsonReader.peek();
                if(JsonToken.STRING.equals(token)){
                    String block = jsonReader.nextString();
                    String[] datas = block.split("\\.");
                    blocks.add(BlockData.createData(Integer.parseInt(datas[2]), Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), new BlockState(StructureLoader.getIdBlock(datas[0]), Integer.parseInt(datas[1]))));
                }
            }
        }catch (IOException e){
            AdaptedScriptAPI.Logger.Log(e.getMessage(), StructureLoader.logger_name);
        }
        return new StructureDescription(StructureUtility.getBlocksByArrayList(blocks));
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }

    @Override
    public String save(StructureDescription stru) {
        String json = "[";
        for(int i = 0;i < stru.blocks.length;i++){
            BlockData data = stru.blocks[i].getData();
            if(i != 0)
                json += ",";
            json += "\""+ StructureLoader.getIdBlock(data.state.id) + "." + data.state.data + "." + data.x + "." + data.y + "." + data.z+"\"";
        }
        json += "]";
        return json;
    }
}
