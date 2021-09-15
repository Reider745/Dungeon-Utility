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
	getBlockByIndex(name, x, y, z){
		let stru = Structure.getStructure(name||[]);
		for(let i in stru)
			if(stru[i].x == x && stru[i].y == y && stru[i].z == z)
				return i;
	},
	setBlockIndex(stru, i, state, extra, tag){
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
	getBlockIndex(name, i){
		return Structure.getStructure(name||[])[i];
	}
};