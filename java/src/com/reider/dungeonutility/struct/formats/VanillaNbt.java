package com.reider.dungeonutility.struct.formats;

import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.du_v2.util.State;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class VanillaNbt extends LoaderType {
    private static InputStream detectDecompression(InputStream is) throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(is, 2);
        int signature = (pbis.read() & 255) + (pbis.read() << 8);
        pbis.unread(signature >> 8);
        pbis.unread(signature & 255);
        return (InputStream)(signature == 35615 ? new GZIPInputStream(pbis) : pbis);
    }

    @Override
    public StructureDescription read(byte[] file, String path) {
        try {
            final NBTInputStream inputStream = new NBTInputStream(detectDecompression(new ByteArrayInputStream(file)));
            CompoundTag tag = (CompoundTag) inputStream.readTag(512).getTag();
            inputStream.close();

            final HashMap<Integer, BlockState> states = new HashMap<>();
            final ListTag<CompoundTag> palette = (ListTag<CompoundTag>) tag.getListTag("palette");

            for(int i = 0;i < palette.size();i++){
                final CompoundTag state = palette.get(i);

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

    @Override
    public byte[] save(StructureDescription stru) {
        return new byte[0];
    }

    @Override
    public boolean isLoadRuntime() {
        return true;
    }
}
