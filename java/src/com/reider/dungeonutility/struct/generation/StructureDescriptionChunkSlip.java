package com.reider.dungeonutility.struct.generation;

import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.generation.stand.api.BaseStand;

import java.util.ArrayList;

public class StructureDescriptionChunkSlip extends StructureDescription {
    private ArrayList<BlockData>[][] blocks_cache;
    public int x_offset, z_offset;
    public int max_x, max_z;
    private int length_x, length_z;

    public StructureDescriptionChunkSlip(StructureDescription stru) {
        super(stru);
        updateCache();
    }


    // Сортировка при регистрации, для более производительного разбиения на чанки
    public final void updateCache(){
        final int[] x = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
        final int[] z = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};

        for(BlockData block : blocks){
            x[0] = Math.min(x[0], block.x);
            x[1] = Math.max(x[1], block.x);

            z[0] = Math.min(z[0], block.z);
            z[1] = Math.max(z[1], block.z);
        }

        x_offset = Math.abs(x[0]);
        z_offset = Math.abs(z[0]);
        max_x = Math.abs(x[1]);
        max_z = Math.abs(z[1]);
        length_x = x_offset + max_x + 1;
        length_z = z_offset + max_z + 1;
        blocks_cache = new ArrayList[length_x][length_z];
        length_x--;
        length_z--;

        for(BlockData block : blocks) {
            ArrayList<BlockData> list = blocks_cache[block.x + x_offset][block.z + z_offset];
            if(list == null){
                list = new ArrayList<>();
                blocks_cache[block.x + x_offset][block.z + z_offset] = list;
            }
            list.add(block);
        }

        x_offset = x[0];
        z_offset = z[0];
        max_x = x[1];
        max_z = z[1];
    }

    // Бьет структуру на чанки
    public final StructureChunk getChunk(int realChunkX, int realChunkZ, int x_center, int z_center, BaseStand stand){
        final ArrayList<BlockData> blocks = new ArrayList<>();

        final int x_point_min = x_center + x_offset;
        final int start_index_x = Math.max(realChunkX - x_point_min, 0);
        final int end_index_x = Math.min(realChunkX + 16 - x_point_min, length_x);

        final int z_point_min = z_center + z_offset;
        final int start_index_z = Math.max(realChunkZ - z_point_min, 0);
        final int end_index_z = Math.min(realChunkZ + 16 - z_point_min, length_z);

        for(int x = start_index_x;x <= end_index_x;x++){
            final ArrayList<BlockData>[] line = blocks_cache[x];

            for(int z = start_index_z;z <= end_index_z;z++) {
                ArrayList<BlockData> list = line[z];
                if(list != null)
                    blocks.addAll(list);
            }
        }

        return new StructureChunk(blocks, realChunkX, realChunkZ, stand);
    }
}
