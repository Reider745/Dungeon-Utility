package com.reider.dungeonutility.struct;

import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;

import java.util.ArrayList;

public class StructurePrivate {
    private static class Position {
        public Position(int x, int y, int z, int x2, int y2, int z2, int dimension){
            this.x = x;
            this.y = y;
            this.z = z;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.dimension = dimension;
        }
        int x;
        int y;
        int z;
        int x2;
        int y2;
        int z2;
        int dimension;
    }

    public static ArrayList<Position> postions = new ArrayList();

    public static void addRegion(int x1, int y1, int z1, int x2, int y2, int z2, NativeBlockSource region){
        postions.add(new Position(x1, y1, z1, x2, y2, z2, region.getDimension()));
    }

    public static Integer getRegionIDtoCoords(int x, int y, int z, NativeBlockSource region){
        for(int i = 0;i < postions.size();i++){
            Position pos = postions.get(i);
            if(pos.dimension != region.getDimension())
                continue;
            if((pos.x <= x) && (pos.x2 >= x) && (pos.y <= y) && (pos.y2 >= y) && (pos.z <= z) && (pos.z2 >= z))
                return i;
        }
        return null;
    }

    public static void deleteRegion(int x, int y, int z, NativeBlockSource region){
        Integer id = getRegionIDtoCoords(x, y, z, region);
        if(id != null)
            postions.remove(id);
    }
    public static boolean isBlockDestroy(int x, int y, int z, NativeBlockSource region){
        return getRegionIDtoCoords(x, y, z, region) != null;
    }
}
