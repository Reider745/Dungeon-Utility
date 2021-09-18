let StructureEditor = WRAP_JAVA("com.reider.dungeon_core.StructureEditor");
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