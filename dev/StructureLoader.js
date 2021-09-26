let BlockData = WRAP_JAVA("com.reider.dungeon_core.BlockData");
let StructureLoaderJava = WRAP_JAVA("com.reider.dungeon_core.StructureLoader");

let loadStructure = {};
let loadReg = [];
Callback.addCallback("LevelPreLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	if(__config__.get("debug.info_load"))
		Logger.Log("start load", "DungeonUtility")
	for(let i in loadReg){
		/*try{
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
		}*/
		loadStructure[loadReg[i].name] = StructureLoader.loadRuntime(loadReg[i].path, loadReg[i].name, loadReg[i].type)
	}
	if(__config__.get("debug.info_load"))
		Logger.Log("end load", "DungeonUtility")
	Callback.invokeCallback("StructureLoad")
});
let StructureLoader = {
	save(path, name, type){
		try{
			FileTools.WriteText(path, StructureLoaderJava.getType(type||"DungeonUtility").save(Structure.getStructure(name||[])), false)
		}catch(error){
			Logger.Log("not enough data to convert or an error occurred", "DungeonUtility")
		}
	},
	registerType(name, obj){
		obj = obj || {};
		StructureLoaderJava.registerType(new StructureLoaderJava.TypeJs(name, obj.read||function(){return[]}, obj.save||function(){return"[]"}))
	},
	load(path, name, type){
		loadReg.push({name: name, path: path, type: type || "DungeonUtility"})
	},
	loadRuntime(path, name, type){
		loadStructure[name] = StructureLoaderJava.load(path, name, type||"DungeonUtility", __config__.get("debug.message_error_load"))
	}
};