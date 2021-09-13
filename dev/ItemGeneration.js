var TYPE = {
  helmet: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 6, l: 3}, {e: 8, l: 1}, {e: 17, l: 3}],
  chestplate: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 17, l: 3}],
  leggings: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 17, l: 3}],
  boots: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 2, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, 7, {e: 17, l: 3}],
  sword: [{e: 9, l: 5}, {e: 10, l: 5}, {e: 11, l: 5}, {e: 12, l: 2}, {e: 13, l: 2}, {e: 14, l: 3}, {e: 17, l: 3}],
  shovel: [{e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  pickaxe: [{e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  axe: [{e: 9, l: 5}, {e: 10, l: 5}, {e: 11, l: 5}, {e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  hoe: [{e: 17, l: 3}],
  bow: [{e: 17, l: 3}, {e: 19, l: 5}, {e: 18, l: 2}, {e: 21, l: 1}, {e: 22, l: 1}],
  fishing: [{e: 17, l: 3}, {e: 23, l: 3}, {e: 24, l: 3}],
  shears: [{e: 15, l: 5}, {e: 17, l: 3}],
};
let generators = {};
let ItemGeneration = {
	newGenerator(name){
		generators[name] = {
			items: [],
			prot: {
				before: function(pos, region, packet){},
				after: function(pos, region, packet){},
				isGenerate: function(pos, random, slot, item, region, packet){return true},
				generate: function(pos, random, slot, item, region, packet){}
			}
		};
	},
	getItems(name){
		return generators[name].items;
	},
	setItems(name, items){
		generators[name].items = items;
	},
	addItem(name, id, random, count, data, extra){
		count = count || {};
		count.min = count.min || 1;
		count.max = count.max || 2;
		count.slotMax = count.slotMax || 2;
		count.slotMin = count.slotMin || 1;
		generators[name].items.push([id || 0, random || 1, count, data || 0, extra || null]);
	},
	setPrototype(name, obj){
		if(!obj.before) obj.before = function(pos, region, packet){}
		if(!obj.after) obj.after = function(pos, region, packet){}
		if(!obj.isGenerate) obj.isGenerate = function(pos, random, slot, item, region, packet){return true}
		if(!obj.generate) obj.generate = function(pos, random, slot, item, region, packet){}
		generators[name].prot = obj;
	},
	getPrototype(name){
		return generators[name].prot;
	},
	fill(name, x, y, z, random, region, packet){
		region = region || BlockSource.getCurrentWorldGenRegion();
		packet = packet || {};
		random = random || new java.util.Random();
		let container = World.getContainer(x, y, z, region);
		let gen = generators[name];
		if(container){
			gen.prot.before({x: x, y: y, z: z}, region, packet);
			for(let i in gen.items){
				let countSlot = random.nextInt(gen.items[i][2].slotMax-gen.items[i][2].slotMin)+gen.items[i][2].slotMin;
				for(let c = 0;c < countSlot;c++){
					let rand = random.nextFloat();
					if(rand <= gen.items[i][1]){
						let slot = random.nextInt(container.getSize());
						let item = {
							id: gen.items[i][0],
							data: gen.items[i][3],
							extra: gen.items[i][4]
						};
						if(gen.prot.isGenerate({x: x, y: y, z: z}, rand, slot, item, region, packet))
							container.setSlot(slot, item.id, random.nextInt(gen.items[i][2].max-gen.items[i][2].min)+gen.items[i][2].min, item.data, item.extra);
						gen.prot.generate({x: x, y: y, z: z}, rand, slot, item, region, packet)
					}
				}
			}
			gen.prot.after({x: x, y: y, z: z}, region, packet)
		}
	},
	enchantAdd(type, count){
		let arr = TYPE[type];
		let extra = new ItemExtraData();
		for(var i=0;i<=count;i++){
			let r = Math.ceil(Math.random()*(arr.length-1));
			let lvl = Math.ceil(Math.random()*(arr[r].l))+1;
			if(arr[r]){
				if(arr[r].e)
					extra.addEnchant(arr[r].e, lvl);
			} 
		}
		return extra;
	}
};