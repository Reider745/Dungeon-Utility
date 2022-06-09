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

StructureLoaderJava.registerType("DungeonAPI", new com.reider.dungeonutility.struct.formats.DungeonAPI());
StructureLoaderJava.registerType("DungeonAPI_V2", new com.reider.dungeonutility.struct.formats.DungeonAPI_V2());
StructureLoaderJava.registerType("DungeonCore", new com.reider.dungeonutility.struct.formats.DungeonCore());
StructureLoaderJava.registerType("Structures", new com.reider.dungeonutility.struct.formats.Structures());
StructureLoaderJava.registerType("DungeonUtility", new com.reider.dungeonutility.struct.formats.DungeonUtility());