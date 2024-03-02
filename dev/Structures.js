let StructureJava = WRAP_JAVA("com.reider.dungeonutility.struct.Structure");
let DefaultStructurePrototype = WRAP_JAVA("com.reider.dungeonutility.struct.structures.DefaultStructurePrototype");
let StructureDestructibilityJava = WRAP_JAVA("com.reider.dungeonutility.struct.structures.StructureDestructibility");

let blockSource = function(){
	return BlockSource.getCurrentWorldGenRegion();
}

function StructureDestructibility(){
	let self = new StructureDestructibilityJava();
	this.addBlock = function(id, state){
		self.addBlock(id, state);
		return this;
	}
	this.get = function(){
		return self;
	}
}

let Structure = {
	getPrototypeDefault(obj){
		return new DefaultStructurePrototype(obj.isItems, obj.name ? obj.name : null, obj.blocks);
	},
	setStructure(name, x, y, z, region, packet){
		StructureJava.setStructure(StructureLoader.getStructure(name), x||0, y||0,z||0, region||blockSource(), packet||{});
	},
	build(name, x, y, z, sleep, region, packet){
		Threading.initThread("Structure-build", function(){
			StructureJava.build(StructureLoader.getStructure(name), x||0, y||0,z||0, region||blockSource(), sleep, packet||{});
		});
	},
	isStructure(name, x, y, z, region){
		return StructureJava.isStructure(StructureLoader.getStructure(name), x||0, y||0, z||0, region||blockSource())==1;
	},
	isSetStructure(name, x, y, z, region){
		return StructureJava.isSetStructure(StructureLoader.getStructure(name), x||0, y||0, z||0, region||blockSource())==1;
	},
	destroy(name, x, y, z, region){
		StructureJava.destroy(StructureLoader.getStructure(name), x||0, y||0, z||0, region||blockSource());
	},
	getStructure(name){
		if(!Array.isArray(name))
			return StructureLoader.getStructure(name).blocks;
		return [];
	},
	addFeatureHandler(obj){
		obj.isBlock = obj.isBlock || function(){return true};
		/*
		Удалён по двум причинам
		1. Жрёт дохуя ресурсов
		2. Некому нахуй не нужен
		*/
		//NativeAPI.addFeature(obj);
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
		if(name instanceof com.reider.dungeonutility.api.StructureDescription)
			var stru = new StructureJava(name);
		else{
			var stru = new StructureJava(new StructureDescription([]));
			if(StructureLoader.isLoad(name))
				stru.setStructure(StructureLoader.getStructure(name));
			else
				Callback.addCallback("StructureLoad", function(){
					stru.setStructure(StructureLoader.getStructure(name));
				});
		}
		/*if(StructureLoader.isLoad(name))
			stru.setStructure(StructureLoader.getStructure(name));
		else
			Callback.addCallback("StructureLoad", function(){
				stru.setStructure(StructureLoader.getStructure(name));
			});*/
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
		this.isSetStructure = function(x, y, z, region){
			return stru.isSetStructure(x||0, y||0, z||0, region||blockSource());
		}
		this.setStructure = function(x, y, z, region, packet){
			stru.setStructure(x||0, y||0, z||0, region||blockSource, packet||{});
			return this;
		}
		this.build = function(x, y, z, sleep, region, packet){
			Threading.initThread("Structure-build", function(){
				stru.build(x||0, y||0, z||0, region||blockSource, sleep, packet||{});
			});
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
Structure.set = Structure.setStructure;
Structure.is = Structure.isStructure;
Structure.isSet = Structure.isSetStructure;
Structure.canSet = Structure.isSetStructure;