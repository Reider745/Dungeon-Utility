let StructureLoaderJava = WRAP_JAVA("com.reider.dungeonutility.StructureLoader");
let StructurePoolJava = WRAP_JAVA("com.reider.dungeonutility.StructurePool");
let BlockData = WRAP_JAVA("com.reider.dungeonutility.api.data.BlockData");
let StructureDescription = WRAP_JAVA("com.reider.dungeonutility.api.StructureDescription");
let StructureCompile = WRAP_JAVA("com.reider.dungeonutility.struct.formats.StructureCompile");

Callback.addCallback("tick", function(){
	while(StructureLoaderJava.isStopTick()){}
});
Callback.addCallback("GenerateChunkUniversal", function(){
	while(StructureLoaderJava.isStopTick()){}
});
Callback.addCallback("LevelPreLoaded", function(){
	//StructureLoaderJava.debugFormats(__dir__+"java/");
	StructureLoaderJava.loadedStructure();
});

function StructurePool(name, global){
	if(name instanceof com.reider.dungeonutility.StructurePool)
		var pool = name;
	else
		var pool = new StructurePoolJava(name, !global);
	this.put = function(name,  stru){
		pool.setStructure(name, stru);
		return this;
	}
	this.get = function(name){
		return pool.getStructure(name);
	}
	this.isLoad = function(name){
		return pool.isLoad(name) == 1;
	}
	this.deLoad = function(name){
		pool.deLoad(name);
		return this;
	}
	this.getAllStructure = function(){
		return pool.getAllStructure();
	}
	this.loadRuntime = function(name, path, type, compile){
		pool.loadRuntime(name, path, type, compile);
		return this;
	}
	this.load = function(path, name, type, compile){
		pool.load(name, path, type||"DungeonUtility", !!compile);
		return this;
	}
	this.copy = function(name1, name2, prot){
		prot = prot || {};
		prot.copyBlock = prot.copyBlock || function(data){
			return data;
		}
		prot.copyPrototype = prot.copyPrototype || function(prot){
			return prot;
		}
		pool.copy(name1, name2, prot);
	},
	this.StructureAdvanced = function(name){
		return new Structure.advanced(this.get(name));
	},
	this.registerRotations = function(stru, rotates){
		rotates = rotates || StructureRotation.getAllY();
		StructureUtilityJava.registerRotations(StructureLoader.getStructure(stru), stru, rotates);
	}
	this.setGlobalPrototype = function(name, obj){
		obj.isBlock = obj.isBlock || function(){return true};
		try{
			Callback.addCallback("StructureLoad", function(){
				pool.setGlobalPrototype(name, obj);
			});
		}catch(e){
			pool.setGlobalPrototype(name, obj);
		}
	}
}

let StructureLoader = {
	getStructurePoolByName(name){
		return new StructurePool(StructureLoaderJava.getStructurePoolByName(name||"default"));
	},
	getAllPool(){
		return StructureLoaderJava.getAllPool();
	},
	getAllStructureAndPool(){//HashMap<String, String[]>
		return StructureLoaderJava.getAllStructureAndPool();
	},
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
		obj.isLoadRuntime = obj.isLoadRuntime || function(){return false;}
		StructureLoaderJava.registerType(name, obj);
	},
	load(path, name, type, compile){
		StructureLoaderJava.load(name, path, type||"DungeonUtility", !!compile);
	},
	loadRuntime(path, name, type, compile){
		type = type||"DungeonUtility";
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(path))
				StructureLoaderJava.loadRuntime(name, path, type, !!compile);
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
		if(name instanceof com.reider.dungeonutility.api.StructureDescription)
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
