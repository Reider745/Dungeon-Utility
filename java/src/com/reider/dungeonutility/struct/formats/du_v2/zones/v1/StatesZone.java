package com.reider.dungeonutility.struct.formats.du_v2.zones.v1;


import com.reider.dungeonutility.api.StateManager;
import com.reider.dungeonutility.api.Utils;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.CompoundTagJson;
import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;
import com.reider.dungeonutility.struct.formats.du_v2.util.State;
import com.reider.dungeonutility.struct.formats.du_v2.zones.BaseZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.IBinaryDungeonUtility;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;
import com.zhekasmirnov.innercore.api.nbt.NativeCompoundTag;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class StatesZone extends BaseZone {
    private static class StorageStateBlock implements IBinaryDungeonUtility {
        private final boolean compression;
        private BlockState main, extra;
        private NativeCompoundTag tag;
        private String tagJson;

        public StorageStateBlock(boolean compression, BlockState main, BlockState extra, NativeCompoundTag tag){
            this.compression = compression;
            this.main = main;
            this.extra = extra;
            this.tag = tag;

            try {
                if(tag != null)
                    tagJson = CompoundTagJson.getMapTag(tag).toString();
            }catch (Exception e){
                Logger.warning(ICLog.getStackTrace(e));
            }
        }

        public StorageStateBlock(boolean compression){
            this.compression = compression;
        }

        @Override
        public void read(ByteBuffer buffer) {
            tagJson = null;

            boolean isMain = buffer.get() == 1;
            boolean isExtra = buffer.get() == 1;
            boolean isTag = buffer.get() == 1;

            if(isMain)
                main = State.read(compression, buffer);
            if(isExtra)
                extra = State.read(compression, buffer);
            if(isTag){
                try {
                    tag = CompoundTagJson.parse(new JSONObject(Utils.readString(buffer)));
                    tagJson = CompoundTagJson.getMapTag(tag).toString();
                }catch (JSONException e){
                    Logger.warning("Error loaded state: "+ICLog.getStackTrace(e));
                }
            }
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put((byte) (main != null ? 1 : 0));
            buffer.put((byte) (extra != null ? 1 : 0));
            buffer.put((byte) (tagJson != null ? 1 : 0));

            if(main != null)
                State.write(compression, main, buffer);
            if(extra != null)
                State.write(compression, extra, buffer);
            if(tagJson != null)
                Utils.putString(buffer, tagJson);
        }

        @Override
        public int mathLength() {
            int length = 3;
            if(main != null)
                length += State.mathLength(compression, main);
            if(extra != null)
                length += State.mathLength(compression, extra);
            if(tagJson != null)
                length += Utils.mathLength(tagJson);
            return length;
        }

        @Override
        public String toString() {
            return "StorageStateBlock{" +
                    "compression=" + compression +
                    ", main=" + main +
                    ", extra=" + extra +
                    ", tagJson='" + tagJson + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof BlockData){
                final BlockData data = (BlockData) obj;
                try{
                    if((data.tag != null && tagJson != null && !tagJson.equals(CompoundTagJson.getMapTag(data.tag).toString())) || (data.tag != null && tagJson == null))
                        return false;
                }catch(JSONException ignore){}

                return State.equals(main, data.state) && State.equals(extra, data.stateExtra);
            }
            return super.equals(obj);
        }
    }


    public static final byte ID_BYTE = 1;
    public static final byte ID_SHORT = 2;

    private static final short NOT_BLOCK = Byte.MIN_VALUE;
    private static final StorageStateBlock DEF_STATE = new StorageStateBlock(false, StateManager.EMPTY_STATE, null, null);

    private final HashMap<Short, StorageStateBlock> states = new HashMap<>();
    private byte type_states = ID_BYTE;
    private short id = NOT_BLOCK + 1;
    private int x_offset, y_offset;
    private boolean compression = false;

    public short addState(BlockData block) {
        for(Short id : states.keySet())
            if(states.get(id).equals(block))
                return id;

        final short current_id = id++;
        states.put(current_id, new StorageStateBlock(compression, block.state, block.stateExtra, block.tag));
        if (current_id >= Byte.MAX_VALUE)
            type_states = ID_SHORT;
        return current_id;
    }

    public short getStateForBlock(BlockData block){
        if(block == null) return NOT_BLOCK;

        for(Short id : states.keySet())
            if(states.get(id).equals(block))
                return id;
        return NOT_BLOCK;
    }

    public byte getTypeStates() {
        return type_states;
    }

    @Override
    public void read(ByteBuffer buffer) {
        final byte type_states = buffer.get();
        final short size = buffer.getShort();

        for(short key = 0;key < size;key++){
            short id;
            if(type_states == ID_BYTE)
                id = buffer.get();
            else
                id = buffer.getShort();
            final StorageStateBlock stateBlock = new StorageStateBlock(compression);
            stateBlock.read(buffer);
            states.put(id, stateBlock);
        }
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.put(type_states);
        buffer.putShort((short) states.size());

        for(Short key : states.keySet()){
            if(type_states == ID_BYTE)
                buffer.put(key.byteValue());
            else
                buffer.putShort(key);

            states.get(key).write(buffer);
        }
    }

    @Override
    public int mathLength() {
        int count = 1;
        count += 2;

        for(Short key : states.keySet()){
            count += type_states;
            count += states.get(key).mathLength();
        }

        return count;
    }

    @Override
    public byte getId() {
        return CompatibilityBase.STATES;
    }

    @Override
    public void preInfo(CompatibilityBase compatibility) {
        final DescriptionZone description = compatibility.getDescription();
        x_offset = description.getXOffset();
        y_offset = description.getYOffset();
        compression = description.canCompressionStates();
    }

    @Override
    public void addInfo(CompatibilityBase compatibility) {
        compatibility.setStates(this);
    }

    public void putBlock(ByteBuffer buffer, BlockData block) {
        if(type_states == ID_BYTE) {
            buffer.put((byte) getStateForBlock(block));
        }else{
            buffer.putShort(getStateForBlock(block));
        }
    }

    public BlockData getBlockForId(ByteBuffer buffer, int x, int y, short z) {
        short id;
        if(type_states == ID_BYTE) {
            id = buffer.get();
        }else{
            id = buffer.getShort();
        }

        if(id == NOT_BLOCK) return null;
        StorageStateBlock stateBlock = states.getOrDefault(id, DEF_STATE);
        return BlockData.createData(x - x_offset, y - y_offset, z, stateBlock.main, stateBlock.extra, stateBlock.tag);
    }

    @Override
    public String toString() {
        String content = super.toString();
        content += NOT_BLOCK+" = null\n";
        for(Short state : states.keySet()){
            content += state + " = "+states.get(state).toString()+"\n";
        }
        return content+"\n\n";
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
