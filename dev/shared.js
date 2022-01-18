ModAPI.registerAPI("DungeonUtility", {
	StructureLoader: StructureLoader,
	Structure: Structure,
	ItemGeneration: ItemGeneration,
	VisualStructure: VisualStructure,
	StructureUtility: StructureUtility,
	PrototypeJS: WRAP_JAVA("com.reider.dungeon_utility.api.PrototypeJS"),
	requireGlobal(command){
		return eval(command);
	},
	getDir(){
		return __dir__;
	},
	version: 2
});
IDRegistry.genItemID("dungeon_utility_wood");
Item.createItem("dungeon_utility_wood", "Dungeon wood \n /struct save name:string save_air:bool specialSeparator:bool type:string, compile:bool", {
	name: "axe", 
	meta: 0
}, {
	stack: 1,
	isTech: false 
});
let firstClick = false;
let coordinates = [{x:0,y:0,z:0},{x:0,y:0,z:0}];
let origin = {x:0,y:0,z:0};
Callback.addCallback("ItemUse", function(coords, item, block, isExter, player){
	if(item.id == ItemID.dungeon_utility_wood && Entity.getSneaking(player)){
		origin = coords;
		Game.message("установлен цент структуры");
	}else if(item.id == ItemID.dungeon_utility_wood && !Entity.getSneaking(player)){
		if(!firstClick){
			coordinates[1] = coords;
			Game.message("вторая точка");
		}else{
			Game.message("первая точка");
			coordinates[0]=coords;
		}
		firstClick = !firstClick;
	}
});
Callback.addCallback("NativeCommand", function(cmd){
	let arr = cmd.split(" ")
	try{
		if(arr[0] == "/struct"){
			if(arr[1] == "save"){
				Game.prevent()
StructureLoader.setStructure(arr[2], new StructureDescription(StructureUtility.getStructureByPos(coordinates, origin, arr[3] == "true")));
				StructureLoader.save(__dir__+"output/"+arr[2]+".struct", arr[2], arr[5] || "DungeonUtility", arr[4] == "true")
				if(arr[6])
					StructureLoader.compile(__dir__+"output/"+arr[2]+".struct");
				Game.message("Структура сохранена")
				//FileTools.WriteJSON(__dir__+"assets/cache.json", cache, false);
			}else if(arr[1] == "load"){
				Game.prevent();
				Game.message("structure load")
			}
		}
	}catch(e){
		Game.message(e);
	}
})
