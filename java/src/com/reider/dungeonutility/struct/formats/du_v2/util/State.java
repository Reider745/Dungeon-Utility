package com.reider.dungeonutility.struct.formats.du_v2.util;

import com.zhekasmirnov.apparatus.adapter.innercore.game.block.BlockState;

import java.nio.ByteBuffer;

public class State {
    public static BlockState read(boolean compression, ByteBuffer buffer){
        return new BlockState(buffer.getShort(), buffer.get());
    }

    public static void write(boolean compression, BlockState state, ByteBuffer buffer){
        buffer.putShort((short) state.id);
        buffer.put((byte) state.data);
    }

    public static int mathLength(boolean compression, BlockState state) {
        return 3;
    }

    public static boolean equals(BlockState data, BlockState data2){
        return data.id == data2.id && data.data == data2.data;
    }
}
