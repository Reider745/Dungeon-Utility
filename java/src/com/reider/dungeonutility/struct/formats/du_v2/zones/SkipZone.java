package com.reider.dungeonutility.struct.formats.du_v2.zones;

import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;

import java.nio.ByteBuffer;

public class SkipZone extends BaseZone {
    @Override
    public void read(ByteBuffer buffer) {}

    @Override
    public void write(ByteBuffer buffer) {}

    @Override
    public void addInfo(CompatibilityBase compatibility) {}

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int mathLength() {
        return 0;
    }

    @Override
    public byte getId() {
        return CompatibilityBase.EMPTY;
    }

    @Override
    public String toString() {
        return "====SKIP ZONE====\n";
    }
}
