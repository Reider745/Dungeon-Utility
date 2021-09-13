/*
BUILD INFO:
  dir: dev
  target: main.js
  files: 5
*/



// file: StructureLoader.js

let BlockData = WRAP_JAVA("com.reider.dungeon_core.BlockData");

let loadStructure = {};
let loadReg = [];
Callback.addCallback("LevelLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	if(__config__.get("debug.message"))
		alert("start load")
	for(let i in loadReg){
		let start = new Date().getTime();
		loadStructure[loadReg[i].name] = StructureLoader["get"+loadReg[i].func](loadReg[i].path)
		if(__config__.get("debug.message"))
			alert("load: "+loadReg[i].name+", type: "+loadReg[i].func+", time: "+((new Date().getTime())-start))
	}
	if(__config__.get("debug.message"))
		alert("end load")
	Callback.invokeCallback("StructureLoad")
});
let StructureLoader = {
	getDungeonAPI(path){
		let arr = [];
		let stru = FileTools.ReadText(path).split(":");
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(new BlockData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : BlockID[data[0]], parseInt(data[1])), new BlockState(0, {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	getDungeonCore(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path);
		for(let i in stru){
			stru[i][3] = stru[i][3] || []
			stru[i][3][0] = stru[i][3][0] || 0
			stru[i][3][1] = stru[i][3][1] || {}
			arr.push(new BlockData(parseInt(stru[i][1].split(".")[1]), parseInt(stru[i][1].split(".")[2]), parseInt(stru[i][1].split(".")[3]), new BlockState(typeof(stru[i][0]) == "string" ? BlockID[stru[i][0]] : stru[i][0], stru[i][2]), new BlockState(stru[i][3][0] == "string" ? BlockID[stru[i][3][0]] : stru[i][3][0], stru[i][3][1]), new NBT.CompoundTag()))
		}
		return arr;
	},
	getDungeonUtility(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path);
		for(let i in stru){
			let data = stru[i][0].split(".")
			arr.push(new BlockData(parseInt(data[2])||0, parseInt(data[3])||0, parseInt(data[4])||0, new BlockState(typeof(parseInt(data[0])||(data[0]==""?0:data[0])) == "number" ? parseInt(data[0])||0 : BlockID[data[0]], stru[i][1] || {}), new BlockState(typeof(parseInt(data[1])||(data[1]==""?0:data[1])) == "number" ? parseInt(data[1])||0 : BlockID[data[1]], stru[i][2] || {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	getStructures(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path).structure;
		for(let i in stru){
			arr.push(new BlockData(stru[i][0], stru[i][1], stru[i][2], new BlockState(typeof(stru[i][3]) == "string" ? BlockID[stru[i][3]] : (typeof(stru[i][3]) == "object" ? stru[i][3].id : 0), stru[i][3].id ? stru[i][3].data : 0), new BlockState(0, {}), new NBT.CompoundTag()));
		}
		return arr;
	},
	load(path, name, type){
		loadReg.push({name: name, path: path, func: type || "DungeonUtility"})
	}
};




// file: Structures.js

let StructureJava = WRAP_JAVA("com.reider.dungeon_core.Dungeon");
let StructureProJava = WRAP_JAVA("com.reider.dungeon_core.DungeonCore");
StructureJava = new StructureJava();
let StructureProtJs = WRAP_JAVA("com.reider.dungeon_core.StructurePrototypeJSAdapter");

let Structure = {
	setStructure(name, x, y, z, region){
		if(typeof(name) == "string")
			StructureJava.setStructure(loadStructure[name] || [], x, y, z, region)
		else
			StructureJava.setStructure(name, x, y, z, region)
	},
	isStructure(name, x, y, z, region){
		if(typeof(name) == "string")
			StructureJava.isStructure(loadStructure[name] || [], x, y, z, region)
		else
			StructureJava.isStructure(name, x, y, z, region)
	},
	getStructure(stru){
		if(typeof(stru) == "string")
			return loadStructure[stru] || [];
		else
			return stru;
	},
	getRandomCoords(x, z, random, obj){
		obj = obj || {}
		return GenerationUtils.findSurface(x*16 + random.nextInt(16), random.nextInt((obj.max||100) - (obj.min||50)) + (obj.min||50), z*16 + random.nextInt(16));
	},
	advanced(name){
		let stru = new StructureProJava(Structure.getStructure(name||[]));
		try{
			Callback.addCallback("StructureLoad", function(){
				stru.setStructure(Structure.getStructure(name||[]));
			})
		}catch(error){
			
		}
		this.setStruct = function(name){
			stru.setStructure(Structure.getStructure(name||[]))
			return this;
		}
		this.setPrototype = function(obj){
			stru.setPrototype(new StructureProtJs(obj.isBlock || function(){return true},obj.setBlock || function(){}, obj.after || function(){}, obj.before || function(){}))
			return this;
		}
		this.getPrototype = function(){
			let prot = stru.getPrototype()
			return {
				isBlock: prot.isSetBlockFunc,
				setBlock: prot.setBlockFunc,
				after: prot.afterFunc,
				before: prot.beforeFunc
			};
		}
		this.isStructure = function(x, y, z, region){
			stru.isStructure(x, y, z, region)
			return this;
		}
		this.isStructureFull = function(x, y, z, region){
			stru.isStructureFull(x, y, z, region)
			return this;
		}
		this.setStructure = function(x, y, z, region, packet){
			stru.setStructure(x, y, z, region, packet || {})
			return this;
		}
		this.getStructure = function(){
			return stru.getStructure();
		}
	},
	GenerateType: {
		OverworldFind(obj){
			obj = obj || {};
			this.min = obj.min || 60;
			this.max = obj.max || 80;
			this.chance = obj.chance || 1000;
			this.white_list = obj.white_list || false;
			this.biome_list = obj.biome_list || []
			this.stru = obj.stru || new Structure.advanced("");
			this.count = obj.count || 1;
			this.isSet = obj.isSet || function(){return true}
			let thas = this;
			this.update = function(){
				thas = this;
			}
			Callback.addCallback("GenerateChunk", function(chunkX, chunkZ, random){
				for(let i = 0;i < thas.count;i++){
				if(random.nextInt(thas.chance) <= 1){
					let coords = GenerationUtils.findSurface(chunkX*16 + random.nextInt(16), random.nextInt(thas.max - thas.min) + thas.min, chunkZ*16 + random.nextInt(16));
					let region = BlockSource.getCurrentWorldGenRegion()
					if(!thas.isSet(coords, random, region))
						return;
					if(thas.white_list){
						if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) != -1){
							thas.stru.setStructure(coords.x, coords.y, coords.z, region);
						}
					}else if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) == -1){
							thas.stru.setStructure(coords.x, coords.y, coords.z, region);
						}
				} 
				}
			});
		},
		CustomDimensionFind(obj){
			obj = obj || {};
			this.min = obj.min || 60;
			this.max = obj.max || 80;
			this.chance = obj.chance || 1000;
			this.white_list = obj.white_list || false;
			this.biome_list = obj.biome_list || []
			this.dimension = obj.dimension || 0;
			this.stru = obj.stru || new Structure.advanced("");
			this.count = obj.count || 1;
			this.isSet = obj.isSet || function(){return true}
			let thas = this;
			this.update = function(){
				thas = this;
			}
			Callback.addCallback("GenerateCustomDimensionChunk", function(chunkX, chunkZ, random, id){
				for(let i = 0;i < thas.count;i++){
				if(id != thas.dimension)
					return
				if(random.nextInt(thas.chance) <= 1){
					let coords = GenerationUtils.findSurface(chunkX*16 + random.nextInt(16), random.nextInt(thas.max - thas.min) + thas.min, chunkZ*16 + random.nextInt(16));
					let region = BlockSource.getCurrentWorldGenRegion()
					if(!thas.isSet(coords, random, region))
						return;
					if(thas.white_list){
						if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) != -1){
							thas.stru.setStructure(coords.x, coords.y, coords.z, region);
						}
					}else if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) == -1){
						thas.stru.setStructure(coords.x, coords.y, coords.z, region);
					}
				} 
				}
			});
		}
	}
}




// file: StructureUtility.js

let StructureUtility = {
	getStructureByName(name){
		return loadStructure[name] || [];
	},
	newStructure(name){
		loadStructure[name] = [];
	},
	getCountBlock(stru){
		return Structure.getStructure(stru||[]).length;
	},
	addBlock(stru, x, y, z, state, extra, tag){
		extra = extra || new BlockState(0, {});
		tag = tag || new NBT.CompoundTag();
		if(typeof(stru) == "string")
			loadStructure[stru].push(new BlockData(x, y, z, state, extra, tag))
		else
			stru.push(new BlockData(x, y, z, state, extra, tag))
	},
	setBlock(stru, x, y, z, state, extra, tag){
		extra = extra || new BlockState(0, {});
		tag = tag || new NBT.CompoundTag();
		if(typeof(stru) == "string"){
			let arr = loadStructure[stru];
			for(let i in arr)
				if(arr[i].x == x && arr[i].y == y && arr[i].z == z)
					loadStructure[stru][i] = new BlockData(x, y, z, state, extra, tag)
		}else{
			for(let i in stru)
				if(stru[i].x == x && stru[i].y == y && stru[i].z == z)
					stru[i] = new BlockData(x, y, z, state, extra, tag)
		}
	},
	getBlock(name, x, y, z){
		let stru = Structure.getStructure(name||[]);
		for(let i in stru)
			if(stru[i].x == x && stru[i].y == y && stru[i].z == z)
				return stru[i];
	},
	getBlockIndex(name, x, y, z){
		let stru = Structure.getStructure(name||[]);
		for(let i in stru)
			if(stru[i].x == x && stru[i].y == y && stru[i].z == z)
				return i;
	},
	setBlock(stru, i, state, extra, tag){
		extra = extra || new BlockState(0, {});
		tag = tag || new NBT.CompoundTag();
		if(typeof(stru) == "string"){
			let arr = loadStructure[stru];
			if(arr[i].x == x && arr[i].y == y && arr[i].z == z)
				loadStructure[stru][i] = new BlockData(x, y, z, state, extra, tag)
		}else{
			if(stru[i].x == x && stru[i].y == y && stru[i].z == z)
				stru[i] = new BlockData(x, y, z, state, extra, tag)
		}
	},
	getBlockByIndex(name, i){
		return Structure.getStructure(name||[])[i];
	}
};




// file: ItemGeneration.js

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




// file: shared.js

ModAPI.registerAPI("DungeonUtility", {
	StructureLoader: StructureLoader,
	Structure: Structure,
	ItemGeneration: ItemGeneration,
	StructureUtility: StructureUtility,
	requireGlobal(command){
		return eval(command);
	},
	version: 1
});
function getId(id){
	if(id >= 8000){
		let keys = Object.keys(BlockID)
		for(let i in keys){
			if(BlockID[keys[i]] == id)
				return keys[i]
		}
	}
	return id;
}
IDRegistry.genItemID("dungeon_utility_wood"); 
Item.createItem("dungeon_utility_wood", "Dungeon wood \n /struct save name:string save_air:bool specialSeparator:bool", {
	name: "axe", 
	meta: 0
}, {
	stack: 1,
	isTech: false 
});
let firstClick = false;
let coordinates = [{x:0,y:0,z:0},{x:0,y:0,z:0}];
let origin = {x:0,y:0,z:0};
Callback.addCallback("ItemUse", function(coords, item, block, isExter, player){
	if(item.id == ItemID.dungeon_utility_wood && Entity.getSneaking(player)){
		origin = coords;
		Game.message("установлен цент структуры");
	}else if(item.id == ItemID.dungeon_utility_wood && !Entity.getSneaking(player)){
		if(!firstClick){
			coordinates[1] = coords;
			Game.message("вторая точка");
		}else{
			Game.message("первая точка");
			coordinates[0]=coords;
		}
		firstClick = !firstClick;
	}
});
Callback.addCallback("NativeCommand", function(cmd){
	let arr = cmd.split(" ")
	try{
		if(arr[0] == "/struct"){
			if(arr[1] == "save"){
				Game.prevent()
				let stru = [];
				for(y = Math.min(coordinates[0].y, coordinates[1].y); y<=Math.max(coordinates[0].y, coordinates[1].y);y++){
					for(x = Math.min(coordinates[0].x, coordinates[1].x); x<=Math.max(coordinates[0].x, coordinates[1].x);x++){
						for(z = Math.min(coordinates[0].z, coordinates[1].z); z<=Math.max(coordinates[0].z, coordinates[1].z);z++){
							let region =  BlockSource.getDefaultForActor(Player.get());
							let str = "";
							let block = region.getBlock(x, y, z);
							let extra_block = region.getExtraBlock(x, y, z)
							let data = block.id;
							if(data!=0)
								str+=getId(data)+"."
							else
								str+="."
							data = extra_block.id;
							if(data!=0)
								str+=getId(data)+"."
							else
								str+="."
							if(x-origin.x!=0)
								str+=x-origin.x+"."
							else
								str+="."
							if(y-origin.y!=0)
								str+=y-origin.y+"."
							else
								str+="."
							if(z-origin.z!=0)
								str+=z-origin.z
							let blockData=[str];
							if(JSON.stringify(block.getNamedStatesScriptable())!="{}")
								blockData.push(block.getNamedStatesScriptable());
							if(JSON.stringify(extra_block.getNamedStatesScriptable())!="{}"){
								if(blockData.length == 1)
									blockData.push({});
								blockData.push(extra_block.getNamedStatesScriptable());
							}
							if(arr[3] == "false"){
								if(block.id != 0){
									stru.push(blockData);
								}
							}else{
								stru.push(blockData);
							}
						}
					}
				}
				FileTools.WriteJSON(__dir__+"output/"+arr[2]+".struct", stru, arr[4] == "true");
				Game.message("Структура сохранена")
			}
		}
	}catch(e){
		Game.message(e);
	}
})




