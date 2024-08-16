package com.reider.dungeonutility.items;

import java.util.ArrayList;
import java.util.Random;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeItemInstanceExtra;
import com.zhekasmirnov.innercore.api.NativeTileEntity;

public class Generator {
    public static class ItemGen {
        private final int id, data, min, max, slotMin, slotMax;
        private final float chance;
        private final NativeItemInstanceExtra extra;

        public ItemGen(int id, int data, float chance, int min, int max, int slotMin, int slotMax, NativeItemInstanceExtra extra){
            this.id = id;
            this.data = data;
            this.chance = chance;
            this.min = min;
            this.max = max;
            this.slotMin = slotMin;
            this.slotMax = slotMax;
            this.extra = extra;
        }

        public int getId() {
            return id;
        }
        public int getData() {
            return data;
        }
        public float getChance() {
            return chance;
        }
        public int getMin() {
            return min;
        }
        public int getMax() {
            return max;
        }
        public int getSlotMin() {
            return slotMin;
        }
        public int getSlotMax() {
            return slotMax;
        }
        public NativeItemInstanceExtra getExtra() {
            return extra;
        }

        private int rand_pre = 0;

        public int getCount(Random random){
            int rand = getMax();
            try {
                rand = random.nextInt(rand-getMin()+1)+getMin();
            }catch (Exception ignore){}
            this.rand_pre = rand;
            return rand;
        }

        public Object getItemInstance(Random random){
            return DungeonUtilityMain.getPackVersionApi()
                    .makeItemInstance(getId(), getCount(random), getData(), getExtra());
        }
    }

    public ArrayList<ItemGen> items = new ArrayList<>();
    private IPrototype prot;
    public boolean infinityFill = true;


    public Generator(){
        prot = new IPrototype() {
            public void before(Vector3 pos, NativeBlockSource region, Object packet){

            }
	        public void after(Vector3 pos, NativeBlockSource region, Object packet){

            }
	        public boolean isGenerate(Vector3 pos, float random, int slot, Object item, NativeBlockSource region, Random rand, Object packet){
                return true;
            }
	        public void generate(Vector3 pos, float random, int slot, Object item, NativeBlockSource region, Random rand, Object packet){
                
            }
        };
    }
    public Generator addItem(ItemGen gen){
        items.add(gen);
        return this;
    }
    public void setPrototype(IPrototype prot){
        this.prot = prot;
    }
    public IPrototype getPrototype(){
        return prot;
    }

    public boolean fillContainer(NativeBlockSource region, Vector3 pos, NativeTileEntity container, Random random, Object packet){
        boolean empty = true;

        for (ItemGen item : items) {
            int countSlot = item.getSlotMax();
            try {
                countSlot = random.nextInt(countSlot - item.getSlotMin()) + item.getSlotMin();
            } catch (Exception ignore) {}

            for (int c = 0; c < countSlot; c++) {
                float rand = random.nextFloat();

                if (rand <= item.getChance()) {
                    int slot = random.nextInt(container.getSize());
                    Object instance = item.getItemInstance(random);
                    if (prot.isGenerate(pos, rand, slot, instance, region, random, packet))
                        container.setSlot(slot, item.getId(), item.rand_pre, item.getData(), item.getExtra());
                    prot.generate(pos, rand, slot, instance, region, random, packet);

                    empty = false;
                }
            }
        }

        return empty;
    }

    public void fill(int x, int y, int z, Random random, NativeBlockSource region, Object packet){
        final NativeTileEntity container = region.getBlockEntity(x, y, z);
		if(container != null){
            final Vector3 pos = new Vector3(x, y, z);
			prot.before(pos, region, packet);

            while (fillContainer(region, pos, container, random, packet) && infinityFill){}


			prot.after(pos, region, packet);
		}
    }
}
