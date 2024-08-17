package com.reider.dungeonutility.struct.formats.du_v2.zones;

import java.nio.ByteBuffer;

public interface IBinaryDungeonUtility {
    void read(ByteBuffer buffer);
    void write(ByteBuffer buffer);
    int mathLength();
}