package com.reider.dungeonutility.struct.formats.du_v2.zones.v1;

import com.reider.dungeonutility.api.data.BlockData;
import com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase;
import com.reider.dungeonutility.struct.formats.du_v2.zones.BaseZone;

import java.nio.ByteBuffer;

public class PlaneBlocksZone extends BaseZone {
    private StatesZone statesZone;
    private short z;
    private short size_max;
    private BlockData[][] blocks;

    public PlaneBlocksZone(){}

    public PlaneBlocksZone(short z, BlockData[][] blocks, short size_max, StatesZone statesZone){
        this.statesZone = statesZone;
        this.z = z;
        this.size_max = size_max;
        this.blocks = blocks;
    }

    public short getZ() {
        return z;
    }

    public BlockData[][] getBlocks() {
        return blocks;
    }


    @Override
    public byte getId() {
        return CompatibilityBase.PLANE_BLOCKS;
    }

    @Override
    public void preInfo(CompatibilityBase compatibility) {
        statesZone = compatibility.getStates();
    }

    @Override
    public void addInfo(CompatibilityBase compatibility) {
        compatibility.addPlane(this);

        /*System.out.println("====PlaneBlocksZone====");
        System.out.println("z: "+z);
        System.out.println("blocks: "+Arrays.deepToString(blocks));
        System.out.println("height: "+blocks.length);
        System.out.println("width: "+this.size_max);
        System.out.println("mathLength: "+mathLength());
        System.out.println("====END PlaneBlocksZone====");*/
    }

    @Override
    public void read(ByteBuffer buffer) {
        z = buffer.getShort();
        int length = buffer.getShort();
        this.size_max = buffer.getShort();

        blocks = new BlockData[length][size_max];

        for(int y = 0;y < length;y++){
            final BlockData[] line = blocks[y];

            for(int x = 0;x < size_max;x++){
                line[x] = statesZone.getBlockForId(buffer, x, y, z);
            }
        }
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putShort(z);
        buffer.putShort((short) blocks.length);
        buffer.putShort(this.size_max);

        for (final BlockData[] line : blocks) {
            for (int i2 = 0; i2 < line.length; i2++) {
                statesZone.putBlock(buffer, line[i2]);
            }
        }
    }

    @Override
    public int mathLength() {
        int count = 2;
        count += 2;
        count += 2;

        final int size_block = statesZone.getTypeStates();
        count += blocks.length * size_max * size_block;

        return count;
    }

    @Override
    public String toString() {
        String content = super.toString();
        for (final BlockData[] line : blocks) {
            String line_str = "";

            for (int i2 = 0; i2 < line.length; i2++) {
                line_str += "."+statesZone.getStateForBlock(line[i2]);
            }

            content += line_str +"\n";
        }
        return content+"\n\n";
    }
}

