package com.reider.dungeonutility.struct.generation;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.types.IGenerationDescription;
import com.reider.dungeonutility.struct.prototypes.IStructurePrototype;
import com.reider.dungeonutility.struct.prototypes.StructurePrototypeEmpty;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class StructureChunk {
    private final ArrayList<BlockData> blocks;
    public final int chunkX, chunkZ;

    private int x, y, z;
    private boolean isLast = false;
    private Structure structure;
    private Object packet;
    private String id;

    public StructureChunk(ArrayList<BlockData> blocks, int chunkX, int chunkZ){
        this.blocks = blocks;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public StructureChunk init(IGenerationDescription description, Vector3 pos, Object packet) {
        this.x = (int) pos.x;
        this.y = (int) pos.y;
        this.z = (int) pos.z;
        this.packet = packet;
        this.structure = description.getStructure();
        this.id = description.getUniqueIdentifier();

        return this;
    }

    public void set(NativeBlockSource region) {
        final IStructurePrototype mainPrototype = structure.getPrototype();
        final IStructurePrototype globalPrototype = structure.getStructure().prot;

        if(mainPrototype instanceof StructurePrototypeEmpty && globalPrototype instanceof StructurePrototypeEmpty){
            for(BlockData blockData : blocks){
                blockData.set(x, y, z, region);
            }
            return;
        }

        final boolean isUseGlobal = structure.isUseGlobalPrototype();

        if(isUseGlobal){
            for(BlockData block : blocks) {
                if (globalPrototype.isBlock(new Vector3(x, y, z), block, region, packet) && mainPrototype.isBlock(new Vector3(x, y, z), block, region, packet))
                    block.set(x, y, z, region);
            }
        }else{
            for(BlockData block : blocks) {
                if (mainPrototype.isBlock(new Vector3(x, y, z), block, region, packet))
                    block.set(x, y, z, region);
            }
        }

        if(isLast) {
            mainPrototype.after(x, y, z, region, packet);
            if(isUseGlobal)
                globalPrototype.after(x, y, z, region, packet);
        }
    }

    public final boolean isEmpty(){
        return blocks.isEmpty();
    }

    public final StructureChunk setLast() {
        isLast = true;
        return this;
    }

    public JSONObject toJSON() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("id", id);

        final JSONArray pos = new JSONArray();
        pos.put(x);
        pos.put(y);
        pos.put(z);
        json.put("p", pos);

        json.put("l", isLast);
        json.put("cx", chunkX);
        json.put("cz", chunkZ);

        return json;
    }

    public static StructureChunk fromJSON(JSONObject json) throws JSONException {
        final IGenerationDescription description = StructurePieceController.getPiece().getFromUniqueIdentifier(json.getString("id"));
        if(description == null){
            Logger.warning("Error read StructureChunk, not structure");
            return null;
        }

        final StructureDescriptionChunkSlip slip = (StructureDescriptionChunkSlip) description.getStructure().getStructure();
        final JSONArray pos = json.getJSONArray("p");

        final IJsObject object = DungeonUtilityMain.getPackVersionApi().createObjectEmpty();
        object.setJavaObj("random", new Random());

        return slip.getChunk(json.getInt("cx"), json.getInt("cz"), pos.getInt(0), pos.getInt(2))
                .init(description, new Vector3(pos.getInt(0), pos.getInt(1), pos.getInt(2)),
                        object.passToScript());
    }
}