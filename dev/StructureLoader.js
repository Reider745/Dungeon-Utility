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