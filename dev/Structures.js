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
		this.getJavaStructure = function(){
			return stru;
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