let BlockData = WRAP_JAVA("com.reider.dungeon_core.BlockData");

let loadStructure = {};
let loadReg = [];
Callback.addCallback("LevelLoaded", function(){
	Callback.invokeCallback("StructurePreLoad")
	if(__config__.get("debug.message"))
		alert("start load")
	for(let i in loadReg){
		let start = new Date().getTime();
		loadStructure[loadReg[i].name] = StructureLoader["get"+loadReg[i].func](loadReg[i].path)
		if(__config__.get("debug.message"))
			alert("load: "+loadReg[i].name+", type: "+loadReg[i].func+", time: "+((new Date().getTime())-start))
	}
	if(__config__.get("debug.message"))
		alert("end load")
	Callback.invokeCallback("StructureLoad")
});
let StructureLoader = {
	getDungeonAPI(path){
		let arr = [];
		let stru = FileTools.ReadText(path).split(":");
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(new BlockData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : BlockID[data[0]], parseInt(data[1])), new BlockState(0, {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	getDungeonCore(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path);
		for(let i in stru){
			stru[i][3] = stru[i][3] || []
			stru[i][3][0] = stru[i][3][0] || 0
			stru[i][3][1] = stru[i][3][1] || {}
			arr.push(new BlockData(parseInt(stru[i][1].split(".")[1]), parseInt(stru[i][1].split(".")[2]), parseInt(stru[i][1].split(".")[3]), new BlockState(typeof(stru[i][0]) == "string" ? BlockID[stru[i][0]] : stru[i][0], stru[i][2]), new BlockState(stru[i][3][0] == "string" ? BlockID[stru[i][3][0]] : stru[i][3][0], stru[i][3][1]), new NBT.CompoundTag()))
		}
		return arr;
	},
	getDungeonUtility(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path);
		for(let i in stru){
			let data = stru[i][0].split(".")
			arr.push(new BlockData(parseInt(data[2])||0, parseInt(data[3])||0, parseInt(data[4])||0, new BlockState(typeof(parseInt(data[0])||(data[0]==""?0:data[0])) == "number" ? parseInt(data[0])||0 : BlockID[data[0]], stru[i][1] || {}), new BlockState(typeof(parseInt(data[1])||(data[1]==""?0:data[1])) == "number" ? parseInt(data[1])||0 : BlockID[data[1]], stru[i][2] || {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	getStructures(path){
		let arr = [];
		let stru = FileTools.ReadJSON(path).structure;
		for(let i in stru){
			arr.push(new BlockData(stru[i][0], stru[i][1], stru[i][2], new BlockState(typeof(stru[i][3]) == "string" ? BlockID[stru[i][3]] : (typeof(stru[i][3]) == "object" ? stru[i][3].id : 0), stru[i][3].id ? stru[i][3].data : 0), new BlockState(0, {}), new NBT.CompoundTag()));
		}
		return arr;
	},
	load(path, name, type){
		loadReg.push({name: name, path: path, func: type || "DungeonUtility"})
	}
};