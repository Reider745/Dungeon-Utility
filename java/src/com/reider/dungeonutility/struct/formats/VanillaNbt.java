package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.StructureDescription;

import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.StructureUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import com.zhekasmirnov.horizon.runtime.logger.Logger;
import net.querz.nbt.io.*;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class VanillaNbt extends LoaderType {
    private static InputStream detectDecompression(InputStream is) throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(is, 2);
        int signature = (pbis.read() & 255) + (pbis.read() << 8);
        pbis.unread(signature >> 8);
        pbis.unread(signature & 255);
        return signature == 35615 ? new GZIPInputStream(pbis) : pbis;
    }

    @Override
    public StructureDescription read(byte[] file, String path) {
        try {
            final NBTInputStream inputStream = new NBTInputStream(detectDecompression(new ByteArrayInputStream(file)));
            final CompoundTag tag = (CompoundTag) inputStream.readTag(512).getTag();
            inputStream.close();

            final HashMap<Integer, BlockState> states = new HashMap<>();
            final ListTag<CompoundTag> palette = (ListTag<CompoundTag>) tag.getListTag("palette");

            for(int i = 0;i < palette.size();i++){
                final CompoundTag state = palette.get(i);
                //TODO: Добавить поддержку properties
                states.put(i, StateManager.buildBlockState(state.getShort("id"), state.getShort("meta")));
            }

            final ListTag<CompoundTag> blocks_tag = (ListTag<CompoundTag>) tag.getListTag("blocks");
            final ArrayList<BlockData> blocks = new ArrayList<>();

            for(int i = 0;i < blocks_tag.size();i++){
                final CompoundTag block = blocks_tag.get(i);
                final ListTag<IntTag> pos = (ListTag<IntTag>) block.getListTag("pos");

                blocks.add(BlockData.createData(pos.get(0).asInt(), pos.get(1).asInt(), pos.get(2).asInt(), states.getOrDefault(block.getInt("state"), StateManager.EMPTY_STATE)));
            }

            return new StructureDescription(blocks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Не проверялось
    @Override
    public byte[] save(StructureDescription stru) {
        final CompoundTag tag = new CompoundTag();
        final StructureUtility.Size[] size = StructureUtility.getStructureSize(stru);
        final ListTag<IntTag> size_tag = (ListTag<IntTag>) ListTag.createUnchecked(IntTag.class);
        final ListTag<CompoundTag> palette_tag = (ListTag<CompoundTag>) ListTag.createUnchecked(CompoundTag.class);
        final ListTag<CompoundTag> blocks_tag = (ListTag<CompoundTag>) ListTag.createUnchecked(CompoundTag.class);
        final HashMap<String, Integer> palette = new HashMap<>();

        size_tag.addInt(Math.abs(size[0].min) + Math.abs(size[0].max) + 1);
        size_tag.addInt(Math.abs(size[1].min) + Math.abs(size[1].max) + 1);
        size_tag.addInt(Math.abs(size[2].min) + Math.abs(size[2].max) + 1);

        int id = 0;
        for(BlockData block : stru.blocks) {
            final BlockData data = block.getData();
            final String key = data.state.id+":"+data.state.data;
            final Integer state_id = palette.get(key);

            if(state_id == null) {
                palette.put(key, id);
                final CompoundTag state_tag = new CompoundTag();

                state_tag.putShort("id", (short) data.state.id);
                state_tag.putShort("meta", (short) data.state.data);

                palette_tag.add(state_tag);

                id++;
            }
        }

        //TODO: Добавить поддержку properties
        for(BlockData block : stru.blocks){
            final BlockData data = block.getData();

            final String key = data.state.id+":"+data.state.data;
            final Integer state_id = palette.get(key);

            if(state_id == null){
                Logger.warning("Error state");
                continue;
            }

            final CompoundTag block_tag = new CompoundTag();
            final ListTag<IntTag> pos_tag = (ListTag<IntTag>) ListTag.createUnchecked(IntTag.class);

            pos_tag.addInt(data.x);
            pos_tag.addInt(data.y);
            pos_tag.addInt(data.z);

            block_tag.put("pos", pos_tag);
            block_tag.putInt("state", state_id);

            blocks_tag.add(block_tag);
        }

        tag.put("size", size_tag);
        tag.put("palette", palette_tag);
        tag.put("blocks", blocks_tag);

        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            final NBTOutputStream stream = new NBTOutputStream(byteStream);
            stream.writeTag(tag, 512);
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try{
                byteStream.close();
            }catch (Exception ignore){}
        }
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }
}
