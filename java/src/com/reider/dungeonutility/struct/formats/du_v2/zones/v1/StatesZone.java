package com.reider.dungeonutility.struct.formats.du_v2.zones.v1;


import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;
import com.reider.dungeonutility.struct.formats.du_v2.util.State;
import com.reider.dungeonutility.struct.formats.du_v2.zones.BaseZone;
import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class StatesZone extends BaseZone {
    public static final byte ID_BYTE = 1;
    public static final byte ID_SHORT = 2;

    private static final short NOT_BLOCK = Byte.MIN_VALUE;
    private static final BlockState DEF_STATE = new BlockState(0, 0);

    private final HashMap<Short, BlockState> states = new HashMap<>();
    private byte type_states = ID_BYTE;
    private short id = NOT_BLOCK + 1;
    private int x_offset, y_offset;
    private boolean compression = false;

    public short addState(BlockState block) {
        for(Short id : states.keySet())
            if(State.equals(states.get(id), block))
                return id;

        final short current_id = id++;
        states.put(current_id, block);
        if (current_id >= Byte.MAX_VALUE)
            type_states = ID_SHORT;
        return current_id;
    }

    public short getStateForBlock(BlockData block){
        if(block == null) return NOT_BLOCK;

        for(Short id : states.keySet())
            if(states.get(id).equals(block.state))
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
            states.put(id, State.read(compression, buffer));
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

            State.write(compression, states.get(key), buffer);
        }
    }

    @Override
    public int mathLength() {
        int count = 1;
        count += 2;

        for(Short key : states.keySet()){
            count += type_states;
            count += State.mathLength(compression, states.get(key));
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

        /*System.out.println("====StatesZone====");
        System.out.println("states: "+states);
        System.out.println("mathLength: "+mathLength());
        System.out.println("====END StatesZone====");*/
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
        return BlockData.createData(x - x_offset, y - y_offset, z, states.getOrDefault(id, DEF_STATE));
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
}
