const StructurePieceController = WRAP_JAVA("com.reider.dungeonutility.struct.generation.StructurePieceController");
const DefaultDescription = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.api.DefaultGeneration");
const WorldStructure = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.api.WorldStructure");
const Vector3 = WRAP_JAVA("com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3");

Network.addClientPacket("message", function(text){
	Game.message(text.text);
})

Network.addServerPacket("DungeonUtility.optimization", function(client){
	let player = client.getPlayerUid();
	if(new PlayerActor(player).isOperator()){
		let pos = Entity.getPosition(player);
		StructurePieceJava.algorithmsOptimization(new Vector3(pos.x, pos.y, pos.z));
		let client = Network.getClientForPlayer(player);
		client && client.send("message", {
			text: "Вами была произведена принудительная оптимизация структур."
		});
	}else{
		let client = Network.getClientForPlayer(player);
		client && client.send("message", {
			text: "У вас нет доступа к очистке"
		});
	}
});

Callback.addCallback("GenerateChunk", function(x, z, rand, id){
	StructurePieceController.getPiece().generation(x, z, rand, id);
});
Callback.addCallback("GenerateCustomDimensionChunk", function(x, z, rand, id){
	StructurePieceController.getPiece().generation(x, z, rand, id);
});

let listPiece = [];
let reg = false;

Callback.addCallback("StructureLoad", function(){
	if(reg)
		return;
	reg = true;
	Callback.invokeCallback("StructureLoadOne");
	for(let i in listPiece){
		let pieces = StructurePieceController.getPieces();
		for(let a in pieces)
			StructurePieceController.getPiece(pieces[a]).addGeneration(listPiece[i]);
	}
});

let StructurePiece = {
	registerType(cl){
		let pieces = StructurePieceController.getPieces();
		for(let i in pieces)
			StructurePieceController.getPiece(pieces[i]).registerType(cl);
	},
	getDefault(obj){
		obj.save = obj.save === undefined ? true :  obj.save;
		obj.offset = obj.offset || {};
		if(obj.structure)
			return new DefaultDescription(obj.type || "default", obj.name || "noy_name", obj.offset.x||0, obj.offset.y||0, obj.offset.z||0, Number(obj.chance)||50, obj.distance || 0, !!obj.save, !!obj.isSet, obj.dimension || 0, !!obj.white_list, obj.biomes || [], !!obj.white_list_blocks, obj.blocks || [0], obj.structure.getStructureJava(), !!obj.checkName, obj.optimization === undefined ? true : obj.optimization, !!obj.legacySpawn, obj.clearToMembory || 60000, obj.count||[1], obj.minAndMaxY||[0, 255], obj.legacy_offset === undefined ? true : obj.legacy_offset);
		else{
			Logger.Log("Error StructurePiece register, Structure = undefined or null "+obj.name || "noy_name", "DungeonUtility");
			return null;
		}
	},
	generateStructure(IStru, x, y, z, random, region, packet){
		StructurePieceController.getPiece().spawnStructure(IStru, new Vector3(x, y, z), region, packet, random, region.getDimension());
	},
	register(stru){
		listPiece.push(stru);
	},
	getNearestStructure(x, y, z, region, name, checkName){
		return StructurePieceController.getStorage().getNearestStructure(new Vector3(x, y, z), region.getDimension(), name|| null, !!checkName);
	},
	addStructure(name, x, y, z, region){
		StructurePieceController.getStorage().add(name, x, y, z, region);
	},
	deleteStructure(x, y, z){
		StructurePieceController.getStorage().del(x, y, z);
	}
};

Callback.addCallback("LevelLeft", function () {
	StructurePieceController.getStorage().clear();
	StructurePieceController.getChunkManager().clear();
});

Saver.addSavesScope("DungeonUtility", function(scope){
	let arr = scope.structures;
	let storage = StructurePieceController.getStorage();
	let result = [];
	for(let i in arr){
		let obj = arr[i];
		storage.add(new WorldStructure(new Vector3(obj.pos.x, obj.pos.y, obj.pos.z), obj.name, obj.dimension||0));
	}
	storage.setStructures(result);
}, function(){
	let arr = [];
	let storage = StructurePieceController.getStorage();
	let structures = storage.getStructures();
	for(let i = 0;i < structures.length;i++){
		let object = structures[i];
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