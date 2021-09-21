let StructureEditor = WRAP_JAVA("com.reider.dungeon_core.StructureEditor");

let cache;
if(FileTools.isExists(__dir__+"assets/cache.json")){
	cache = FileTools.ReadJSON(__dir__+"assets/cache.json");
}else{
	Logger.Log("Error open cache", "DungeonUtility");
	cache = {};
}
cache.states = cache.states || {};
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
	getAllStructureName(){
		return Object.keys(loadStructure);
	},
	copy(name, name2){
		loadStructure[name2] = loadStructure[name];
	},
	getStateByData(id, data){
		cache.states[id] = cache.states[id] || {};
		return cache.states[id][data]||{};
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
						cache.states[block.id] = cache.states[block.id] || {};
						if(block.data != 0)
							cache.states[block.id][block.data] = block.getNamedStatesScriptable();
						else
							cache.states[block.id][block.data] = {};
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
	addBlock(stru, x, y, z, state, extra, tag){
		if(typeof(stru) == "string")
			loadStructure[stru].push(new BlockData(x, y, z, state, extra||null, tag||null))
		else
			stru.push(new BlockData(x, y, z, state, extra, tag))
	},
	setBlock(stru, x, y, z, state, extra, tag){
		this.setBlockByIndex(stru, this.getBlockIndex(stru, x, y, z), x, y, z, state, extra, tag)
	},
	getBlock(name, x, y, z){
		return StructureEditor.getBlock(Structure.getStructure(name||[]), new BlockData(x, y, z))
	},
	getBlockIndex(name, x, y, z){
		return StructureEditor.getBlockIndex(Structure.getStructure(name||[]), new BlockData(x, y, z))
	},
	setBlockByIndex(stru, i, x, y, z, state, extra, tag){
		if(typeof(stru) == "string")
			loadStructure[stru][i] = new BlockData(x, y, z, state, extra||null, tag||null);
		else
			stru[i] = new BlockData(x, y, z, state, extra||null, tag||null);
	},
	getBlockByIndex(name, i){
		return Structure.getStructure(name||[])[i];
	}
};