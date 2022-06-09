let StructureUtilityJava = WRAP_JAVA("com.reider.dungeonutility.struct.StructureUtility");
let StructureRotation = WRAP_JAVA("com.reider.dungeonutility.struct.StructureRotation");

let StructureRotationJS = {
	DEFAULT: StructureRotation.DEFAULT,
	DEGREES_90: StructureRotation.DEGREES_90, 
	DEGREES_180: StructureRotation.DEGREES_180, 
	DEGREES_270: StructureRotation.DEGREES_270, 
	DEFAULT_DOWN: StructureRotation.DEFAULT_DOWN, 
	DEGREES_90_DOWN: StructureRotation.DEGREES_90_DOWN, 
	DEGREES_180_DOWN: StructureRotation.DEGREES_180_DOWN, 
	DEGREES_270_DOWN: StructureRotation.DEGREES_270_DOWN,
	getAll(){
		return StructureRotation.getAll();
	},
	getAllY(){
		return StructureRotation.getAllY();
	},
	getAllDown(){
		return StructureRotation.getAllDown();
	},
	getRandomName(rotates, random){
		rotates = rotates || this.getAllY();
		random = random || new java.util.Random();
		return StructureRotation.getRandomName(rotates, random);
	}
};

let StructureUtility = {
	getStructureSize(name){
		return StructureUtilityJava.getStructureSize(StructureLoader.getStructure(name));
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
	registerRotationsRuntime(stru, rotates){
		rotates = rotates || StructureRotation.getAllY();
		StructureUtilityJava.registerRotations(StructureLoader.getStructure(stru), stru, rotates);
	},
	registerRotations(stru, rotates){
		Callback.addCallback("StructureLoad", function(){
			StructureUtility.registerRotationsRuntime(stru, rotates);
		});
	},
	getAllStructureName(){
		return StructureUtilityJava.getAllStructureName();
	},
	copy(name1, name2, prot){
		prot = prot || {};
		prot.copyBlock = prot.copyBlock || function(data){
			return data;
		}
		prot.copyPrototype = prot.copyPrototype || function(prot){
			return prot;
		}
		StructureUtilityJava.copy(name1, name2, prot);
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
	createBlock(x, y, z, state, extra, tag){
		return BlockData.createData(x, y, z, state||null, extra||null, tag||null)
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
	},
	fill(x1, y1, z1, x2, y2, z2, block, region){
		StructureUtilityJava.fill(x1, y1, z1, x2, y2, z2, BlockData.createData(0, 0, 0, block, null, null), region || blockSource());
	},
	fillHandler(x1, y1, z1, x2, y2, z2, block, region, obj){
		StructureUtilityJava.fill(x1, y1, z1, x2, y2, z2, BlockData.createData(0, 0, 0, block, null, null), region || blockSource(), obj);
	}
};
