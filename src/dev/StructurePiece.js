let StructurePieceJava = WRAP_JAVA("com.reider.dungeonutility.struct.generation.StructurePiece");
let WorldStructure = WRAP_JAVA("com.reider.dungeonutility.struct.generation.WorldStructure");
let OverWorld = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.OverWorld");
let DefaultType = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.Default");
let Nether = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.Nether");
let DefaultDescription = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.DefaultGeneration");
let Vector3 = WRAP_JAVA("com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3");

try{
StructurePieceJava.setCriticalReleaseSetting(__config__.get("critical_release.enable") == true, Number(__config__.get("critical_release.active")), Number(__config__.get("critical_release.radius")));
StructurePieceJava.setClearingClustersSetting(__config__.get("critical_release.enable") == true, Number(__config__.get("critical_release.active")), Number(__config__.get("critical_release.radius")));
}catch(e){
	alert(e);
}

Network.addClientPacket("message", function(text){
	Game.message(text.text);
})

Network.addServerPacket("DungeonUtility.optimization", function(client){
	let player = client.getPlayerUid();
	if(player == Player.get()){
		let pos = Entity.getPosition(player);
		StructurePieceJava.algorithmsOptimization(new Vector3(pos.x, pos.y, pos.z));
		let client = Network.getClientForPlayer(player);
		if(client)
			client.send("message", {
				text: "Вами была произведена принудительная оптимизация структур."
			});
	}else{
		let client = Network.getClientForPlayer(player);
		if(client)
			client.send("message", {
				text: "У вас нет доступа к очистке"
			});
	}
});

Callback.addCallback("NativeCommand", function(cmd){
	if(cmd == "/optimization"){
		Network.sendToServer("DungeonUtility.optimization", {});
	}
});

Callback.addCallback("GenerateChunk", function(x, z, rand, id){
	StructurePieceJava.callbackGeneration(x, z, rand, id);
});
Callback.addCallback("GenerateCustomDimensionChunk", function(x, z, rand, id){
	StructurePieceJava.callbackGeneration(x, z, rand, id);
});

let listPiece = [];
let reg = false;

Callback.addCallback("StructureLoad", function(){
	if(reg)
		return;
	reg = true;
	Callback.invokeCallback("StructureLoadOne");
	for(let i in listPiece)
		StructurePieceJava.register(listPiece[i]);
});

let StructurePiece = {
	registerType(cl){
		StructurePieceJava.registerType(cl);
	},
	getDefault(obj){
		obj.save = obj.save === undefined ? true :  obj.save;
		obj.offset = obj.offset || {};
		if(obj.structure)
			return new DefaultDescription(obj.type || "default", obj.name || "noy_name", obj.offset.x||0, obj.offset.y||0, obj.offset.z||0, Number(obj.chance)||50, obj.distance || 0, !!obj.save, !!obj.isSet, obj.dimension || 0, !!obj.white_list, obj.biomes || [], !!obj.white_list_blocks, obj.blocks || [0], obj.structure.getStructureJava(), !!obj.checkName, obj.optimization === undefined ? true : obj.optimization, !!obj.legacySpawn);
		else{
			Logger.Log("Error StructurePiece register, Structure = undefined or null "+obj.name || "noy_name", "DungeonUtility");
			return null;
		}
	},
	generateStructure(IStru, x, y, z, random, region, packet){
		StructurePieceJava.generateStructure(IStru, x, y, z, random, region, packet);
	},
	register(stru){
		listPiece.push(stru);
	},
	getNearestStructure(x, y, z, region, name, checkName){
		return StructurePieceJava.getNearestStructure(new Vector3(x, y, z), region.getDimension(), name|| null, !!checkName);
	},
	addStructure(name, x, y, z, region){
		StructurePieceJava.add(name, x, y, z, region);
	},
	deleteStructure(x, y, z){
		StructurePieceJava.del(x, y, z);
	}
};

Callback.addCallback("LevelLeft", function () {
	StructurePieceJava.structures.clear();
});

Saver.addSavesScope("DungeonUtility", function(scope){
	let arr = scope.structures;
	for(let i in arr){
		let obj = arr[i];
		StructurePieceJava.structures.add(new WorldStructure(new Vector3(obj.pos.x, obj.pos.y, obj.pos.z), obj.name, obj.dimension||0));
	}
}, function(){
	let arr = [];
	let size = StructurePieceJava.structures.size();
	for(let i = 0;i < size;i++){
		let object = StructurePieceJava.structures.get(i);
		arr.push({
			name: String(object.name),
			pos: {
				x: Number(object.pos.x),
				y: Number(object.pos.y),
				z: Number(object.pos.z)
			},
			dimension: Number(object.dimension)
		});
	}
	return {
		structures: arr
	};
});

StructurePiece.registerType(new OverWorld());
StructurePiece.registerType(new Nether());
StructurePiece.registerType(new DefaultType());

let structure = new StructureDescriptionJS();
for(let x = 0;x < 16;x++)
	for(let y = 0;y < 16;y++)
		for(let z = 0;z < 16;z++)
			structure.addBlock(x, y, z, new BlockState(1, 0));

StructurePiece.register(StructurePiece.getDefault({
	distance: 32,
	chance: 2,
	structure: new Structure.advanced(structure.getDescription())
}));