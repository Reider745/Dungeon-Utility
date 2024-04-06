package com.reider.dungeonutility.items;

import java.util.ArrayList;
import java.util.Random;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.NativeItemInstanceExtra;
import com.zhekasmirnov.innercore.api.NativeTileEntity;
import com.zhekasmirnov.innercore.api.commontypes.ItemInstance;

public class Generator {
    public static class ItemGen {
        int id;
        int data;
        float chance;
        int min;
        int max;
        int slotMin;
        int slotMax;
        NativeItemInstanceExtra extra;

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
            int rand = random.nextInt(getMax()-getMin()+1)+getMin();
            return rand;
        }

        public Object getItemInstance(Random random){
            return ItemInstance.make(getId(), getCount(random), getData(), getExtra());
        }
    }
    public ArrayList<ItemGen> items = new ArrayList<>();
    IPrototype prot;
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
    public void fill(int x, int y, int z, Random random, NativeBlockSource region, Object packet){
        NativeTileEntity container = region.getBlockEntity(x, y, z);
		if(container != null){
            Vector3 pos = new Vector3(x, y, z);
			prot.before(pos, region, packet);
			for(int i = 0; i < items.size();i++){
                ItemGen item = items.get(i);
				int countSlot = random.nextInt(item.getSlotMax()-item.getSlotMin())+item.getSlotMin();
				for(int c = 0;c < countSlot;c++){
					float rand = random.nextFloat();
					if(rand <= item.getChance()){
						int slot = random.nextInt(container.getSize());
                        Object instance = item.getItemInstance(random);
						if(prot.isGenerate(pos, rand, slot, instance, region, random,packet))
							container.setSlot(slot, item.getId(), item.rand_pre, item.getData(), item.getExtra());
						prot.generate(pos, rand, slot, instance, region, random, packet);
					}
				}
			}
			prot.after(pos, region, packet);
		}
    }
}
