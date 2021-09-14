let BlockData = WRAP_JAVA("com.reider.dungeon_core.BlockData");

let loadStructure = {};
let loadReg = [];
Callback.addCallback("LevelLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	if(__config__.get("debug.info_load"))
		alert("start load")
	for(let i in loadReg){
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(loadReg[i].path))
				loadStructure[loadReg[i].name] = StructureLoader.types[loadReg[i].type].read(FileTools.ReadText(loadReg[i].path));
			else 
				alert("error path, load structure: "+loadReg[i].name+"");
			if(__config__.get("debug.info_load"))
				alert("load: "+loadReg[i].name+", type: "+loadReg[i].type+", time: "+((new Date().getTime())-start))
		}catch(error){
			if(__config__.get("debug.message_error_load"))
				alert("error load structure: "+loadReg[i].name+"\n"+error);
		}
	}
	if(__config__.get("debug.info_load"))
		alert("end load")
	Callback.invokeCallback("StructureLoad")
});
let StructureLoader = {
	save(path, name, type){
		FileTools.WriteText(path, this.types[type || "DungeonUtility"].save(loadStructure[name]), false)
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
				loadStructure[name] = StructureLoader.types[type].read(FileTools.ReadText(path));
			else 
				alert("error path, load structure: "+name+"");
			if(__config__.get("debug.info_load"))
				alert("load: "+name+", type: "+type+", time: "+((new Date().getTime())-start))
		}catch(error){
			if(__config__.get("debug.message_error_load"))
				alert("error load structure: "+name+"\n"+error);
		}
	}
};