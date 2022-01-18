/*
BUILD INFO:
  dir: dev
  target: main.js
  files: 7
*/



// file: StructureLoader.js

let StructureLoaderJava = WRAP_JAVA("com.reider.dungeon_utility.StructureLoader");
let BlockData = WRAP_JAVA("com.reider.dungeon_utility.api.data.BlockData");
let StructureDescription = WRAP_JAVA("com.reider.dungeon_utility.api.StructureDescription");
let StructureCompile = WRAP_JAVA("com.reider.dungeon_utility.struct.formats.StructureCompile");


Callback.addCallback("LevelPreLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	let stru = StructureLoaderJava.getPreLoads();
	if(__config__.get("debug.info_load"))
		Logger.Log("start load", "DungeonUtility")
	for(let i = 0;i < stru.size();i++){
		let struct = stru.get(i);
		StructureLoader.loadRuntime(struct.path, struct.name, struct.type, struct.compile);
	}
	if(__config__.get("debug.info_load"))
		Logger.Log("end load", "DungeonUtility");
	Callback.invokeCallback("StructureLoad");
});


let StructureLoader = {
	save(path, name, type, compile){
		try{
			FileTools.WriteText(path, StructureLoaderJava.getType(type||"DungeonUtility").save(StructureLoader.getStructure(name)), false)
			if(compile)
				StructureLoader.compile(path);
		}catch(error){
			Logger.Log("error convert "+error, "DungeonUtility")
		}
	},
	registerType(name, obj){
		StructureLoaderJava.registerType(name, obj);
	},
	load(path, name, type, compile){
		compile = compile === undefined || compile === null ? false : compile;
		StructureLoaderJava.load(name, path, type||"DungeonUtility", compile);
	},
	loadRuntime(path, name, type, compile){
		type = type||"DungeonUtility";
		compile = compile === undefined || compile === null ? false : compile;
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(path))
				StructureLoaderJava.loadRuntime(name, path, type, compile);
			else if(__config__.get("debug.info_load"))
				Logger.Log("error path, load structure: "+name, "DungeonUtility");
			if(__config__.get("debug.info_load"))
				Logger.Log("load: "+name+", type: "+type+", time: "+((new Date().getTime())-start), "DungeonUtility");
		}catch(e){
			if(__config__.get("debug.message_error_load"))
				Logger.Log("error load structure: "+name+"\n"+e, "DungeonUtility");
		}
	},
	getStructure(name){
		if(name instanceof com.reider.dungeon_utility.api.StructureDescription)
			return name;
		if(this.isLoad(name||"error"))
			return StructureLoaderJava.getStructure(name||"error");
		Logger.Log("structure noy load "+name, "DungeonUtility");
		alert("error "+name)
		return new StructureDescription([]);
	},
	setStructure(name, stru){
		StructureLoaderJava.loadRuntime(name, stru);
	},
	isLoad(name){
		return StructureLoaderJava.isStructureLoad(name)==1;
	},
	deLoad(name){
		StructureLoaderJava.deLoad(name||"error");
	},
	compile(path){
		StructureCompile.compile(path, FileTools.ReadText(path))
	},
	decompile(path){
		FileTools.WriteText(path, StructureCompile.decompile(path), false);
	}
};




// file: StructureType.js

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
function getState(id, state){
	/*let block_state = new BlockState(id, state);
	if(JSON.stringify(StructureUtility.getStateByData(id, block_state.data))==JSON.stringify(state))
		return block_state.data;*/
	return state;
}

StructureLoader.registerType("DungeonAPI", {
	read(file){
		let arr = [];
		let stru = file.split(":");
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(BlockData.createData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(Block.convertBlockToItemId(parseInt(data[0]) ? parseInt(data[0]) : data[0] == "0" ? 0 : BlockID[data[0]]), parseInt(data[1]))))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let str = "";
		let stru = struct.blocks;
		for(let i in stru){
			let data = stru[i].getData();
			str += getId(data.state.id)+"."+data.state.data+"."+data.x+"."+data.y+"."+data.z;
			if(i == stru.length - 1)
			 str += ":";
		}
		return str;
	}
})

StructureLoader.registerType("DungeonAPI_V2", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(BlockData.createData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : data[0] == "0" ? 0 : BlockID[data[0]], parseInt(data[1]))))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = [];
		for(let i in stru){
			 data = stru[i].getData();
			arr.push(getId(data.state.id)+"."+data.state.data+"."+data.x+"."+data.y+"."+data.z)
		}
		return JSON.stringify(arr);
	}
})

StructureLoader.registerType("DungeonCore", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			stru[i][3] = stru[i][3] || [];
			stru[i][3][0] = stru[i][3][0] || 0;
			stru[i][3][1] = stru[i][3][1] || {};
			arr.push(BlockData.createData(stru[i][1].split(".")[1] == "0" ? 0 : parseInt(stru[i][1].split(".")[1]), stru[i][1].split(".")[2] == "0" ? 0 : parseInt(stru[i][1].split(".")[2]), stru[i][1].split(".")[3] == "0" ? 0 : parseInt(stru[i][1].split(".")[3]), new BlockState(typeof(stru[i][0]) == "string" ? BlockID[stru[i][0]] : stru[i][0], stru[i][2]), new BlockState(stru[i][3][0] == "string" ? BlockID[stru[i][3][0]] : stru[i][3][0], stru[i][3][1])))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = [];
		for(let i in stru){
			 data = stru[i].getData();
			let block = [getId(data.state.id), data.state.data+"."+data.x+"."+data.y+"."+data.z, data.state.getNamedStatesScriptable(), [getId(data.stateExtra.id), data.stateExtra.getNamedStatesScriptable()]];
			arr.push(block);
		}
		return JSON.stringify(arr);
	}
});

StructureLoader.registerType("Structures", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file).structure;
		for(let i in stru){
			arr.push(BlockData.createData(stru[i][0], stru[i][1], stru[i][2], new BlockState(typeof(stru[i][3]) == "string" ? BlockID[stru[i][3]] : (typeof(stru[i][3]) == "object" ? stru[i][3].id : 0), stru[i][3].id ? stru[i][3].data : 0)));
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = {
			version: 3,
			structure: []
		};
		for(let i in stru){
			data = stru[i].getData();
			arr.structure.push([data.x, data.y, data.z, {id: getId(data.state.id), data: data.state.data},null])
		}
		return JSON.stringify(arr);
	}
});

StructureLoader.registerType("DungeonUtility", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			let data = stru[i][0].split(".")
			arr.push(BlockData.createData(parseInt(data[2])||0, parseInt(data[3])||0, parseInt(data[4])||0, new BlockState(typeof(parseInt(data[0])||(data[0]==""?0:data[0])) == "number" ? parseInt(data[0])||0 : BlockID[data[0]], typeof(stru[i][1] || {})=="number" ? stru[i][1] == "0" ? 0 : parseInt(stru[i][1]) : stru[i][1] || {}), new BlockState(typeof(parseInt(data[1])||(data[1]==""?0:data[1])) == "number" ? parseInt(data[1])||0 : BlockID[data[1]], typeof(stru[i][2] || {})=="number" ? stru[i][2] == "0" ? 0 : parseInt(stru[i][2]) : stru[i][2] || {}), null))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = []
		for(let i in stru){
			let str = "";
			let b = stru[i].getData();
			let data = b.state.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			data = b.stateExtra.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			if(b.x!=0)
				str+=b.x+"."
			else
				str+="."
			if(b.y!=0)
				str+=b.y+"."
			else
				str+="."
			if(b.z!=0)
				str+=b.z
			let blockData=[str];
			if(JSON.stringify(b.state.getNamedStatesScriptable())!="{}"||getState(b.state.id,b.state.getNamedStatesScriptable())!=0)
				blockData.push(getState(b.state.id,b.state.getNamedStatesScriptable()));
			if(JSON.stringify(b.stateExtra.getNamedStatesScriptable())!="{}"||getState(b.stateExtra.id,b.stateExtra.getNamedStatesScriptable())!=0){
				if(blockData.length == 1)
					blockData.push(0);
				blockData.push(getState(b.stateExtra.id,b.stateExtra.getNamedStatesScriptable()));
			}
			arr.push(blockData)
		}
		return JSON.stringify(arr);
	}
});




// file: Structures.js

let StructureJava = WRAP_JAVA("com.reider.dungeon_utility.struct.Structure");

let blockSource = function(){
	return BlockSource.getCurrentWorldGenRegion();
}

let Structure = {
	setStructure(name, x, y, z, region, packet){
		StructureJava.setStructure(StructureLoader.getStructure(name), x||0, y||0,z||0, region||blockSource(), packet||{});
	},
	isStructure(name, x, y, z, region){
		return StructureJava.isStructure(StructureLoader.getStructure(name), x||0, y||0, z||0, region||blockSource())==1;
	},
	destroy(name, x, y, z, region){
		StructureJava.destroy(StructureLoader.getStructure(name), x||0, y||0, z||0, region||blockSource());
	},
	getStructure(name){
		if(!Array.isArray(name))
			return StructureLoader.getStructure(name).blocks;
		return [];
	},
	setGlobalPrototype(name, obj){
		obj.isBlock = obj.isBlock || function(){return true};
		try{
			Callback.addCallback("StructureLoad", function(){
				StructureJava.setGlobalPrototype(name, obj);
			});
		}catch(e){
			StructureJava.setGlobalPrototype(name, obj);
		}
	},
	getGlobalPrototype(name){
		return StructureJava.getGlobalPrototype(name);
	},
	advanced(name){
		let stru = new StructureJava(new StructureDescription([]));
		if(StructureLoader.isLoad(name))
			stru.setStructure(StructureLoader.getStructure(name));
		else
			Callback.addCallback("StructureLoad", function(){
				stru.setStructure(StructureLoader.getStructure(name));
			});
		this.getStructureJava = function(){
			return stru;
		}
		this.setUseGlobalPrototype = function(value){
			stru.setUseGlobalPrototype(value);
			return this;
		}
		this.isUseGlobalPrototype = function(){
			return stru.isUseGlobalPrototype();
		}
		//для обратной совместимости 
		this.setPrototype = function(obj){
			obj.isBlock = obj.isBlock || function(){return true};
			const funcIsBlock = obj.isBlock;
			obj.isBlock = function(original_pos, data, region, packet){
				return funcIsBlock(original_pos, {
					x: original_pos.x + data.x,
					y: original_pos.y + data.y,
					x: original_pos.z + data.z
				}, data.state, data.stateExtra, data, region, packet);
			}
			const funcSetBlock = obj.setBlock;
			if(funcSetBlock)
				obj.setBlock = function(original_pos, data, region, packet){
					funcSetBlock(original_pos, {
						x: original_pos.x + data.x,
						y: original_pos.y + data.y,
						z: original_pos.z + data.z
					}, data.state, data.stateExtra, data, region, packet);
			}
			stru.setPrototype(obj);
			return this;
		}
		//новый метод 
		this.setProt = function(obj){
			try{
				obj.isBlock = obj.isBlock || function(){return true};
			}catch(e){
				
			}
			stru.setPrototype(obj);
			return this;
		}
		this.getPrototype = function(){
			return stru.getPrototype();
		}
		this.setStruct = function(name){
			stru.setStructure(Structure.getStructure(name));
			return this;
		}
		this.getStructure = function(){
			return stru.getStructure();
		}
		this.isStructure = function(x, y, z, region){
			return stru.isStructure(x||0, y||0, z||0, region||blockSource());
		}
		this.setStructure = function(x, y, z, region, packet){
			stru.setStructure(x||0, y||0, z||0, region||blockSource, packet||{})
			return this;
		}
		this.destroy = function(x, y, z, region){
			stru.destroy(x||0, y||0, z||0, region||blockSource());
			return this;
		}
	},
	
	getRandomCoords(x, z, random, obj){
		obj = obj || {}
		return GenerationUtils.findSurface(x*16 + random.nextInt(16), random.nextInt((obj.max||100) - (obj.min||50)) + (obj.min||50), z*16 + random.nextInt(16));
	},
	generators: {},
	setStructureGeneration(name, generator){
		this.generators[name] = generator;
	},
	getStructureGeneration(name){
		return this.generators[name];
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
Logger.Log("Dungeon Utility", "Generation "+stru.name);
						if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) != -1){
							thas.stru.setStructure(coords.x, coords.y, coords.z, region, {random: random});
						}
					}else if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) == -1){
							thas.stru.setStructure(coords.x, coords.y, coords.z, region, {random: random});
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
							thas.stru.setStructure(coords.x, coords.y, coords.z, region, {random: random});
						}
					}else if(thas.biome_list.indexOf(region.getBiome(coords.x, coords.z)) == -1){
						thas.stru.setStructure(coords.x, coords.y, coords.z, region, {random: random});
					}
				} 
				}
			});
		}
	}
};




// file: StructureUtility.js

let StructureUtilityJava = WRAP_JAVA("com.reider.dungeon_utility.struct.StructureUtility");

let StructureUtility = {
	getStructureSize(name){
		return StructureUtilityJava.getStructureSize(Structure.getStructure(StructureLoader.getStructure(name)));
	},
	getStructureByName(name){
		return StructureLoader.getStructure(name).blocks;
	},
	newStructure(name, stru){
		StructureLoader.setStructure(name, new StructureDescription(stru || []));
	},
	getCountBlock(stru){
		return this.getStructureByName(stru).length;
	},
	rotate(stru, rotate){
		return StructureUtilityJava.rotate(StructureLoader.getStructure(stru), rotate||0);
	},
	getAllStructureName(){
		return StructureUtilityJava.getAllStructureName();
	},
	copy(name1, name2){
		StructureUtilityJava.copy(name1, name2);
	},
getStructureByPos(pos, cen, value){
		let region =  BlockSource.getDefaultForActor(Player.get());
		let stru = [];
		for(y = Math.min(pos[0].y, pos[1].y); y<=Math.max(pos[0].y, pos[1].y);y++){
			for(x = Math.min(pos[0].x, pos[1].x); x<=Math.max(pos[0].x, pos[1].x);x++){
				for(z = Math.min(pos[0].z, pos[1].z); z<=Math.max(pos[0].z, pos[1].z);z++){
					let block = region.getBlock(x, y, z);
					if(block.id != 0 || value){
						let tile = region.getBlockEntity(x, y, z);
						/*cache.states[block.id] = cache.states[block.id] || {};
						if(block.data != 0)
							cache.states[block.id][block.data] = block.getNamedStatesScriptable();
						else
							cache.states[block.id][block.data] = {};*/
						let tag = null;
						if(tag)
							tag = tile.getCompoundTag()
						stru.push(new BlockData(x-cen.x, y-cen.y, z-cen.z, block, region.getExtraBlock(x, y, z), tag))
					}
				}
			}
		}
		return stru;
	},
	generateShape(region, x, y, z, r, y_max, id, data, dirtId, dirtData, grassId, grassData){
		data = data || 0;
		y_max = y_max || r*2;
		StructureUtilityJava.generateShape(x, y, z, r+2, y_max, new BlockData(0, 0, 0, new BlockState(id, data)), 3, new BlockData(0, 0, 0, new BlockState(dirtId||id, dirtData||data)), new BlockData(0, 0, 0, new BlockState(grassId||id, grassData||data)), region);
	},
	generateShapeOptimization(region, name, x, y, z, r, id, data){
		StructureUtilityJava.generateShapeOptimization(x, y, z, r, new BlockData(0,0,0,new BlockState(id,data)), region);
	},
	spawnEntity(region, x, y, z, ents, random){
		random = random || new java.util.Random();
		StructureUtilityJava.spawnEntity(region, x, y, z, ents||[], random);
	},
	
	addBlock(stru, x, y, z, state, extra, tag){
		StructureUtilityJava.addBlock(StructureLoader.getStructure(stru), BlockData.createData(x, y, z, state||null, extra||null, tag||null));
	},
	setBlock(stru, x, y, z, state, extra, tag){
		StructureUtilityJava.setBlock(StructureLoader.getStructure(stru), BlockData.createData(x, y, z, state||null, extra||null, tag||null));
	},
	getBlock(name, x, y, z){
		return StructureUtilityJava.getBlock(StructureLoader.getStructure(name), x, y, z)
	},
	getBlockIndex(name, x, y, z){
		return StructureUtilityJava.getBlockIndex(StructureLoader.getStructure(name), x, y, z);
	},
	setBlockByIndex(name, i, x, y, z, state, extra, tag){
		StructureUtilityJava.setBlock(StructureLoader.getStructure(name), i, BlockData.createData(x, y, z, state||null, extra||null, tag||null))
	}
};




// file: VisualStructure.js

let VisualStructure = {
	getStructureModel(name){
		let model = BlockRenderer.createModel();
		let stru = Structure.getStructure(name);
		for(let i in stru){
			model.addBox(stru[i].x, stru[i].y, stru[i].z, stru[i].x+1, stru[i].y+1, stru[i].z+1,  stru[i].state.id, stru[i].state.data)
		}
		return model
	},
	getStructureBitmap(name){
		let model = this.getStructureModel(name);
		let render = new ICRender.Model(); 
		render.addEntry(model);
		BlockRenderer.setStaticICRender(5, -1, render);
		let gui = model.buildGuiModel(true, 1024);
		return gui.genTexture(1024, 1024);
	},
	getArrMesh(name, size, value){
		let BaseArr = [];
		let stru = Structure.getStructure(name);
		for(let i = 0;i < stru.length;i++){
			let obj = {state: stru[i].state, pos: [stru[i].x, stru[i].y, stru[i].z]}
			if(stru[i].state.id == 0 || value)
				continue;
			let base = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
			base.describeItem({
				id: Block.convertBlockToItemId(stru[i].state.id),
				data: stru[i].state.data,
				size: size || .95,
				material: "visual_structure"
			});
			obj.base = base;
			try{
				if(stru[i].stateExtra.id != 0){
					obj.extra = stru[i].stateExtra;
					let base_extra = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
					base_extra.describeItem({
						id: Block.convertBlockToItemId(stru[i].stateExtra.id),
						data: stru[i].stateExtra.data,
						size: size || .95,
						material: "visual_structure"
					});
					obj.base_extra = base_extra;
				}
			}catch(error){
				
			}
			BaseArr.push(obj)
		}
		return BaseArr;
	},
	Animation(stru, size, value){
		let BaseArr = VisualStructure.getArrMesh(stru, size, value);
		
		this.loaded = false;
		this.getArrBase = function(){
			return BaseArr;
		}
		this.setStructure = function(stru, size, value){
			this.destroy();
			BaseArr = VisualStructure.getArrMesh(stru, size, value);
		}
		this.getStructure = function(){
			return stru;
		}
		let prot = {
			isLoad(){return true},
			load(){return "visual_structure"},
			tick(){},
			tickBlock(){}
		};
		this.setPrototype = function(obj){
			obj.isLoad = obj.isLoad || function(){return true}
			obj.load = obj.load || function(){return "visual_structure"}
			obj.tick = obj.tick || function(){}
			obj.tickBlock = obj.tickBlock || function(){}
			prot = obj;
		}
		this.getPrototype = function(obj){
			return prot;
		}
		this.load = function(x, y, z, a, packet){
			this.loaded = true;
			this.remove = false
			for(let i in BaseArr){
				let pos = BaseArr[i].pos;
				BaseArr[i].base.setPos(x+pos[0],y+pos[1],z+pos[2]);
				if(prot.isLoad(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr[i].base, i, packet))
					BaseArr[i].base.loadCustom(function(){
						prot.tickBlock(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, this, i, packet)
					});
				let material = prot.load(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr[i].base, i, packet)
				BaseArr[i].base.render.setMaterial(material)
				
				BaseArr[i].base.getShaderUniforms().setUniformValue("visual_structure", "A", a || .6);
			}
			this.update = function(){
				prot.tick(x,y,z, packet)
			}
			Updatable.addUpdatable(this)
		}
		this.destroy = function(){
			if(!this.loaded)
				return
			this.loaded = false;
			this.remove = true
			for(let i in BaseArr){
				BaseArr[i].base.destroy();
			}
		}
	}
};
/*
Пусть будет здесь в качестве примера 
Callback.addCallback("StructureLoad", function(){
	//wood_0 структура 
 let Test = new VisualStructure.Animation("wood_0", 1.1);
 Test.setPrototype({
 	load(x, y, z, org_pos, base){
 		return "visual_structure_noy"
 	},
 	tick(x, y, z, packet){
 		if(World.getThreadTime() % 10 == 0){
 			let arr = Test.getArrBase();
 			let value = false;
 			for(let i in arr){
 				if(value)
 					continue
 				let id = BlockSource.getDefaultForActor(Player.get()).getBlockID(x-.5+arr[i].pos[0], y+arr[i].pos[1], z-.5+arr[i].pos[2]);
 				if(arr[i].state.id != id && id != 0){
 					arr[i].base.render.setMaterial("visual_structure_red");
 					value = true;
 				}else if(arr[i].state.id == id){
 					arr[i].base.render.setMaterial("visual_structure_noy");
 				}else{
 					arr[i].base.render.setMaterial("visual_structure");
 				value = true;
 				}
 			}
 		}
 	}
 })
 Callback.addCallback("ItemUseLocal", function(coords, item){
  if(item.id == 264)
   Test.load(coords.x+.5, coords.y+.5, coords.z+.5)
 })
});
*/




// file: ItemGeneration.js

var __extends = (this && this.__extends) || (function () {

    var extendStatics = function (d, b) {

        extendStatics = Object.setPrototypeOf ||

            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||

            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };

        return extendStatics(d, b);

    };

    return function (d, b) {

        extendStatics(d, b);

        function __() { this.constructor = d; }

        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());

    };

})();
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
	isGenerator(name){
		return !!generators[name];
	},
	importFromFile(name, path){
		Callback.invokeCallback("ImportGeneratorFromFile", name, path);
		if(!this.isGenerator(name))
			this.newGenerator(name);
		const loots = FileTools.ReadJSON(path);
		const items = Object.keys(ItemID);
		const blocks = Object.keys(BlockID);
		for(let i in loots)
			if(loots[i].type == "block")
				this.addItem(name, typeof(loots[i].id) == "number" ? loots[i].id : blocks.indexOf(loots[i].id) != -1 ? BlockID[loots[i].id] : VanillaBlockID[loots[i].id], loots[i].chance, loots[i].count, loots[i].data, loots[i].extra ? (function(){
				let extra = new ItemExtraData();
				extra.setAllCustomData(JSON.stringify(loots[i].extra));
				return extra;
			})() : null);
			else
				this.addItem(name, typeof(loots[i].id) == "number" ? loots[i].id : items.indexOf(loots[i].id) != -1 ? ItemID[loots[i].id] : VanillaItemID[loots[i].id], loots[i].chance, loots[i].count, loots[i].data, loots[i].extra ? (function(){
				let extra = new ItemExtraData();
				extra.setAllCustomData(JSON.stringify(loots[i].extra));
				return extra;
			})() : null);
		this.registerRecipeViewer(name.replace("_", " "), name);
	},
	getAllGenerator(){
		return Object.keys(generators);
	},
	addItem(name, id, random, count, data, extra){
		count = count || {};
		count.min = count.min || 1;
		count.max = count.max+1 || 2;
		count.slotMax = count.slotMax+1 || 2;
		count.slotMin = count.slotMin || 1;
		generators[name].items.push([id || 0, random || 1, count, data || 0, extra || null]);
	},
	setPrototype(name, obj){
		if(!obj.before) obj.before = function(pos, region, packet){}
		if(!obj.after) obj.after = function(pos, region, packet){}
		if(!obj.isGenerate) obj.isGenerate = function(pos, random, slot, item, region, random, packet){return true}
		if(!obj.generate) obj.generate = function(pos, random, slot, item, region, random, packet){}
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
							extra: gen.items[i][4],
							count: random.nextInt(gen.items[i][2].max-gen.items[i][2].min)+gen.items[i][2].min
						};
						if(gen.prot.isGenerate({x: x, y: y, z: z}, rand, slot, item, region, random,packet))
							container.setSlot(slot, item.id, item.count, item.data, item.extra);
						gen.prot.generate({x: x, y: y, z: z}, rand, slot, item, region, random, packet)
					}
				}
			}
			gen.prot.after({x: x, y: y, z: z}, region, packet)
		}else if(__config__.get("debug.message_error_generation_item")){
			Game.message("noy container x:"+x+", y: "+y+", z: "+z)
		}
	},
	registerRecipeViewer(generator, name){
		name = name || "";
		Callback.addCallback('ModsLoaded', function(){
			ModAPI.addAPICallback("RecipeViewer", function(api){
				let arr = ItemGeneration.getItems(generator);
				var RVTypeAW = (function(_super){
  				__extends(RVTypeAW, _super);
  		  	function RVTypeAW(nameRv, icon, content){
     		 	let _this = _super.call(this, nameRv, icon, content) || this;
     		 	return _this;
   		 	}
  		  	RVTypeAW.prototype.getAllList = function(){
    				let list = [];
    				for(let i in arr){
    					list.push({
    						min: arr[i][2].min,
    						max: arr[i][2].max-1,
    						random: (arr[i][1]*100)+"%",
    						input: [],
    						output: [{id: arr[i][0], data: arr[i][3], count: 1}]
    					});
    				}
      			return list;
   		 	};
   		 	RVTypeAW.prototype.onOpen = function(elements, data){
  					elements.get("textMax").onBindingUpdated("text", "max spawn: "+data.max);
        	 	elements.get("textMin").onBindingUpdated("text", "min spawn: "+data.min);
        	 	elements.get("textChance").onBindingUpdated("text", "chance spawn: "+data.random);
  				};
    			return RVTypeAW;
  			}(api.RecipeType));
  			api.RecipeTypeRegistry.register(generator, new RVTypeAW(name, 54, {
  				elements: {
  				output0: {x: 300, y: 150, size: 120},
      	  	textMax: {type: "text", x: 490, y: 110, font: {size: 40}},
       	 	textMin: {type: "text", x: 490, y: 160, font: {size: 40}},
        		textChance: {type: "text", x: 490, y: 210, font: {size: 40}},
        	}
  			}));
  		});
		});
	},
	enchantAdd(type, count){
		let arr = TYPE[type];
		let extra = new ItemExtraData();
		for(let i=0;i<=count;i++){
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
	VisualStructure: VisualStructure,
	StructureUtility: StructureUtility,
	PrototypeJS: WRAP_JAVA("com.reider.dungeon_utility.api.PrototypeJS"),
	requireGlobal(command){
		return eval(command);
	},
	getDir(){
		return __dir__;
	},
	version: 2
});
IDRegistry.genItemID("dungeon_utility_wood");
Item.createItem("dungeon_utility_wood", "Dungeon wood \n /struct save name:string save_air:bool specialSeparator:bool type:string, compile:bool", {
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
StructureLoader.setStructure(arr[2], new StructureDescription(StructureUtility.getStructureByPos(coordinates, origin, arr[3] == "true")));
				StructureLoader.save(__dir__+"output/"+arr[2]+".struct", arr[2], arr[5] || "DungeonUtility", arr[4] == "true")
				if(arr[6])
					StructureLoader.compile(__dir__+"output/"+arr[2]+".struct");
				Game.message("Структура сохранена")
				//FileTools.WriteJSON(__dir__+"assets/cache.json", cache, false);
			}else if(arr[1] == "load"){
				Game.prevent();
				Game.message("structure load")
			}
		}
	}catch(e){
		Game.message(e);
	}
})




