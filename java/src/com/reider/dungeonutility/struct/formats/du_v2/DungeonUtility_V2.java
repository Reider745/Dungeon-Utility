package com.reider.dungeonutility.struct.formats.du_v2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.StructureDescription;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.LoaderType;
import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;
import com.reider.dungeonutility.struct.formats.du_v2.zones.BaseZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.DescriptionZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.PlaneBlocksZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.StatesZone;
import com.reider.dungeonutility.struct.formats.legacy.BlockPalette;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

public class DungeonUtility_V2 extends LoaderType {
    @Override
    public StructureDescription read(byte[] file, String path, BlockPalette palette) {
        ByteBuffer buffer = ByteBuffer.wrap(file);
        byte version = buffer.get();// На тот случай, если внутри сильно что-то изменится

        final CompatibilityBase compatibility = new CompatibilityBase(new HashMap<>());
        compatibility.readZones(buffer);
        return new StructureDescription(compatibility.getBlocks());
    }

    @Override
    public byte[] save(StructureDescription stru) {
        try{
            final HashMap<Short, ArrayList<BlockData>> temp_planes = new HashMap<>();
            final int[] x = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
            final int[] y = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
            final StatesZone states = new StatesZone();

            for(BlockData block : stru.blocks){
                x[0] = Math.min(x[0], block.x);
                x[1] = Math.max(x[1], block.x);

                y[0] = Math.min(y[0], block.y);
                y[1] = Math.max(y[1], block.y);

                temp_planes.computeIfAbsent((short) block.z, k -> new ArrayList<>()).add(block);
                states.addState(block);
            }

            final int offset_x = Math.abs(x[0]);
            final int offset_y = Math.abs(y[0]);
            final int width = offset_x + Math.abs(x[1]) + 1;
            final int height = offset_y + Math.abs(y[1]) + 1;

            final HashMap<Short, PlaneBlocksZone> planes = new HashMap<>();

            for(Short layer : temp_planes.keySet()){
                final BlockData[][] blocks_layer = new BlockData[height][width];
                final ArrayList<BlockData> list = temp_planes.get(layer);

                for (BlockData block : list) {
                    blocks_layer[block.y + offset_y][block.x + offset_x] = block;
                }

                list.clear();

                planes.put(layer, new PlaneBlocksZone(layer, blocks_layer, (short) width, states));
            }

            temp_planes.clear();

            final CompatibilityBase compatibility = new CompatibilityBase(planes);
            final ArrayList<BaseZone> zones = new ArrayList<>();

            final DescriptionZone description = new DescriptionZone();
            description.setCompressionStates(false);
            description.setOffset(offset_x, offset_y);

            zones.add(description);
            zones.add(states);
            zones.addAll(planes.values());

            compatibility.setDescription(description);
            compatibility.setStates(states);

            return compatibility.writeZones(zones).array();
        }catch (Exception e){
            Logger.error(DungeonUtilityMain.logger_name, "Error save DU2\n"+ ICLog.getStackTrace(e));
        }
        return new byte[0];
    }

    @Override
    public boolean isLoadRuntime() {
        return false;
    }
}
