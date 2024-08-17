package com.reider.dungeonutility.struct.formats.du_v2.zones;


import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;

public abstract class BaseZone implements IBinaryDungeonUtility {
    public abstract byte getId();
    public abstract void addInfo(CompatibilityBase compatibility);
    public void preInfo(CompatibilityBase compatibility){}

    @Override
    public String toString() {
        String content = "===="+getClass().getName()+"====\n";
        content += "id = "+getId()+"\n";
        return content;
    }
}