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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    protected final BaseZone parseZone(ByteBuffer buffer, ArrayList<Map.Entry<BaseZone, ByteBuffer>> list){
        final int position = buffer.position();

        if(position >= buffer.capacity())
            return null;

        final byte id = buffer.get();
        final int index_end = buffer.getInt();
        final int size = index_end - position;
        final BaseZone result = buildZone(id);
        final byte[] bytesZone = new byte[size+1];

        for(int i = 0;i < index_end-position;i++)
            bytesZone[i] = buffer.get();

        final Map.Entry<BaseZone, ByteBuffer> element = new AbstractMap.SimpleEntry<>(result, ByteBuffer.wrap(bytesZone));
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getKey().getPriority() < result.getPriority()) {
                list.add(i, element);
                return result;
            }
        }

        list.add(element);

        return result;
    }

    public final void readZones(ByteBuffer buffer){
        final ArrayList<Map.Entry<BaseZone, ByteBuffer>> list = new ArrayList<>();

        BaseZone zone;
        while ((zone = this.parseZone(buffer, list)) != null){
        }


        for(Map.Entry<BaseZone, ByteBuffer> entry : list){
            final BaseZone _zone = entry.getKey();
            try{
                _zone.preInfo(this);
                _zone.read(entry.getValue());
                _zone.addInfo(this);
            }catch (Exception e){
                Logger.debug(DungeonUtilityMain.logger_name,"Error parse zone, id:"+_zone.getId()+"\n"+ICLog.getStackTrace(e));
            }
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

    public byte getVersion(){
        return Byte.MIN_VALUE;
    }

    public final ByteBuffer writeZones(ArrayList<BaseZone> zones){
        ByteBuffer buffer = ByteBuffer.wrap(new byte[mathLength(zones)]);

        buffer.put(getVersion());

        for(BaseZone zone : zones){
            final int position = buffer.position();
            final int length = zone.mathLength();

            buffer.put(zone.getId());
            buffer.putInt(position + length);

            try{
                zone.write(buffer);
            }catch (Exception e){
                Logger.error(ICLog.getStackTrace(e));
            }


            final int takes = buffer.position() - position - 5;
            if(takes != length)
                Logger.warning("Takes bytes: " + takes + ", expected: " + length + ", zone id: " + zone.getId());
        }

        return buffer;
    }

    public int mathLength(ArrayList<BaseZone> zones) {
        int count = 1;//version

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
        content += "version = " + getVersion() + "\n";
        content += description+"\n\n\n";
        content += states+"\n\n\n";

        for(PlaneBlocksZone plane : planes.values())
            content += plane+"\n\n\n";

        return content;
    }
}