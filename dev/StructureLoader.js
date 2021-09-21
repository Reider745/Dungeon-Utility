let BlockData = WRAP_JAVA("com.reider.dungeon_core.BlockData");

let loadStructure = {};
let loadReg = [];
Callback.addCallback("LevelPreLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	if(__config__.get("debug.info_load"))
		Logger.Log("start load", "DungeonUtility")
	for(let i in loadReg){
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(loadReg[i].path))
				loadStructure[loadReg[i].name] = StructureLoader.types[loadReg[i].type].read(FileTools.ReadText(loadReg[i].path));
			else 
				Logger.Log("error path, load structure: "+loadReg[i].name+"", "DungeonUtility");
			if(__config__.get("debug.info_load"))
				Logger.Log("load: "+loadReg[i].name+", type: "+loadReg[i].type+", time: "+((new Date().getTime())-start), "DungeonUtility")
		}catch(error){
			if(__config__.get("debug.message_error_load"))
				Logger.Log("error load structure: "+loadReg[i].name+"\n"+error, "DungeonUtility");
		}
	}
	if(__config__.get("debug.info_load"))
		Logger.Log("end load", "DungeonUtility")
	Callback.invokeCallback("StructureLoad")
});
let StructureLoader = {
	save(path, name, type, value){
		try{
			FileTools.WriteText(path, this.types[type || "DungeonUtility"].save(Structure.getStructure(name||[])), value)
		}catch(error){
			Logger.Log("not enough data to convert or an error occurred", "DungeonUtility")
		}
	},
	types: {},
	registerType(name, obj){
		obj = obj || {};
		obj.save = obj.save || function(){};
		obj.read = obj.read || function(){};
		this.types[name] = obj;
	},
	load(path, name, type){
		loadReg.push({name: name, path: path, type: type || "DungeonUtility"})
	},
	loadRuntime(path, name, type){
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(path))
				loadStructure[name] = StructureLoader.types[type|| "DungeonUtility"].read(FileTools.ReadText(path))
			else 
				Logger.Log("error path, load structure: "+name+"", "DungeonUtility");
			if(__config__.get("debug.info_load"))
				Logger.Log("load: "+name+", type: "+type+", time: "+((new Date().getTime())-start), "DungeonUtility")
		}catch(error){
			if(__config__.get("debug.message_error_load"))
				Logger.Log("error load structure: "+name+"\n"+error, "DungeonUtility");
		}
	}
};