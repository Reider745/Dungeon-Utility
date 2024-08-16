package com.reider.dungeonutility.struct;

import java.util.Random;

public enum StructureRotation {
    DEFAULT,
    DEGREES_90,
    DEGREES_180,
    DEGREES_270,
    DEFAULT_DOWN,
    DEGREES_90_DOWN,
    DEGREES_180_DOWN,
    DEGREES_270_DOWN;

    public static StructureRotation[] getAll(){
        return new StructureRotation[] {DEFAULT, DEGREES_90, DEGREES_180, DEGREES_270, DEFAULT_DOWN, DEGREES_90_DOWN, DEGREES_180_DOWN, DEGREES_270_DOWN};
    }
    public static StructureRotation[] getAllY(){
        return new StructureRotation[] {DEFAULT, DEGREES_90, DEGREES_180, DEGREES_270};
    }
    public static StructureRotation[] getAllYDown(){
        return new StructureRotation[] {DEFAULT_DOWN, DEGREES_90_DOWN, DEGREES_180_DOWN, DEGREES_270_DOWN};
    }
    public static String getRandomName(StructureRotation[] rotate, Random random){
        return rotate[random.nextInt(rotate.length)].name();
    }
    public static String getRandomName(StructureRotation[] rotate){
        return getRandomName(rotate, new Random());
    }
}
