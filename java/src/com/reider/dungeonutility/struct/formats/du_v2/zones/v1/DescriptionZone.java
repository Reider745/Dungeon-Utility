package com.reider.dungeonutility.struct.formats.du_v2.zones.v1;

import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;
import com.reider.dungeonutility.struct.formats.du_v2.zones.MapZone;

public class DescriptionZone extends MapZone {
    private static final byte EXTREME_COMPRESSION_STATES = 0;
    private static final byte X_OFFSET = 1;
    private static final byte Y_OFFSET = 2;

    public boolean canCompressionStates(){
        return getBoolean(EXTREME_COMPRESSION_STATES);
    }

    public void setCompressionStates(boolean value){
        put(EXTREME_COMPRESSION_STATES, value);
    }

    public void setOffset(int x, int y) {
        put(X_OFFSET, (short) x);
        put(Y_OFFSET, (short) y);
    }

    public int getXOffset(){
        return getShort(X_OFFSET);
    }

    public int getYOffset(){
        return getShort(Y_OFFSET);
    }

    @Override
    public byte getId() {
        return CompatibilityBase.DESCRIPTION;
    }

    @Override
    public void addInfo(CompatibilityBase compatibility) {
        compatibility.setDescription(this);

        /*System.out.println("====DescriptionZone====");
        System.out.println("canCompressionStates: "+this.canCompressionStates());
        System.out.println("x: "+this.getXOffset()+", y: "+this.getYOffset());
        System.out.println("mathLength: "+mathLength());
        System.out.println("====END DescriptionZone====");*/
    }

    @Override
    public String toName(byte id) {
        switch (id){
            case EXTREME_COMPRESSION_STATES:
                return "EXTREME_COMPRESSION_STATES";
            case X_OFFSET:
                return "X_OFFSET";
            case Y_OFFSET:
                return "Y_OFFSET";
        }
        return super.toName(id);
    }
}