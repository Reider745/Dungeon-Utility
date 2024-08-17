package com.reider.dungeonutility.struct.formats.du_v2.compatibility;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.du_v2.zones.BaseZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.SkipZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.DescriptionZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.PlaneBlocksZone;
import com.reider.dungeonutility.struct.formats.du_v2.zones.v1.StatesZone;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.log.ICLog;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class CompatibilityBase {
    public static final byte EMPTY = -1;
    public static final byte DESCRIPTION = 0;
    public static final byte STATES = 1;
    public static final byte PLANE_BLOCKS = 2;

    private static final HashMap<Byte, Function<Void, BaseZone>> registerZones = new HashMap<>();

    public static void register(byte id, Function<Void, BaseZone> constructor){
        registerZones.put(id, constructor);
    }

    static {
        register(EMPTY, v -> new SkipZone());
        register(DESCRIPTION, v -> new DescriptionZone());
        register(STATES, v -> new StatesZone());
        register(PLANE_BLOCKS, v -> new PlaneBlocksZone());
    }

    private DescriptionZone description;
    private StatesZone states;
    private final HashMap<Short, PlaneBlocksZone> planes;

    public CompatibilityBase(HashMap<Short, PlaneBlocksZone> planes){
        this.planes = planes;
    }

    protected BaseZone buildZone(byte id){
        Function<Void, BaseZone> zone = registerZones.get(id);
        if(zone == null)
            return new SkipZone();
        return zone.apply(null);
    }

    protected final BaseZone parseZone(ByteBuffer buffer){
        final int position = buffer.position();

        if(position >= buffer.capacity())
            return null;

        final byte id = buffer.get();
        final int index_end = buffer.getInt();
        final int size = index_end - position;
        final BaseZone result = buildZone(id);

        byte[] bytesZone = new byte[size+1];
        for(int i = 0;i < index_end-position;i++)
            bytesZone[i] = buffer.get();

        try{
            result.preInfo(this);
            result.read(ByteBuffer.wrap(bytesZone));
            result.addInfo(this);
        }catch (Exception e){
            Logger.debug(DungeonUtilityMain.logger_name,"Error parse zone, id:"+id+"\n"+ICLog.getStackTrace(e));
        }


        return result;
    }

    public final void parseZones(ByteBuffer buffer){
        BaseZone zone;
        while ((zone = this.parseZone(buffer)) != null){
        }
    }

    public void setDescription(DescriptionZone description) {
        this.description = description;
    }

    public void setStates(StatesZone states) {
        this.states = states;
    }

    public DescriptionZone getDescription() {
        return description;
    }

    public StatesZone getStates() {
        return states;
    }

    public final ByteBuffer writeZones(ArrayList<BaseZone> zones){
        ByteBuffer buffer = ByteBuffer.wrap(new byte[mathLength(zones)]);

        for(BaseZone zone : zones){
            final int position = buffer.position();
            final int length = zone.mathLength();

            buffer.put(zone.getId());
            buffer.putInt(position + length);

            zone.write(buffer);

            //System.out.println("Takes bytes: " + (buffer.position() - position - 5) + ", expected: "+length+", zone id: "+zone.getId());
        }

        return buffer;
    }

    public int mathLength(ArrayList<BaseZone> zones) {
        int count = 0;

        for(BaseZone zone : zones){
            count += 1;
            count += 4;
            count += zone.mathLength();
        }

        return count;
    }

    public void addPlane(PlaneBlocksZone plane){
        planes.put(plane.getZ(), plane);
    }

    private static final BlockData[] EMPTY_BLOCK = new BlockData[0];
    public BlockData[] getBlocks(){
        ArrayList<BlockData> blocks = new ArrayList<>();

        for(PlaneBlocksZone plane : planes.values())
            for(BlockData[] plane_blocks : plane.getBlocks())
                for(BlockData block : plane_blocks)
                    if(block != null)
                        blocks.add(block);

        return blocks.toArray(EMPTY_BLOCK);
    }

    @Override
    public String toString() {
        String content = "";
        content += description.toString()+"\n\n\n";
        content += states.toString()+"\n\n\n";

        for(PlaneBlocksZone plane : planes.values())
            content += plane.toString()+"\n\n\n";

        return content;
    }
}