ModAPI.registerAPI("DungeonUtility", {
	StructureLoader: StructureLoader,
	Structure: Structure,
	ItemGeneration: ItemGeneration,
	StructureUtility: StructureUtility,
	requireGlobal(command){
		return eval(command);
	},
	version: 1
});
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
IDRegistry.genItemID("dungeon_utility_wood"); 
Item.createItem("dungeon_utility_wood", "Dungeon wood \n /struct save name:string save_air:bool specialSeparator:bool", {
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
				let stru = [];
				for(y = Math.min(coordinates[0].y, coordinates[1].y); y<=Math.max(coordinates[0].y, coordinates[1].y);y++){
					for(x = Math.min(coordinates[0].x, coordinates[1].x); x<=Math.max(coordinates[0].x, coordinates[1].x);x++){
						for(z = Math.min(coordinates[0].z, coordinates[1].z); z<=Math.max(coordinates[0].z, coordinates[1].z);z++){
							let region =  BlockSource.getDefaultForActor(Player.get());
							let str = "";
							let block = region.getBlock(x, y, z);
							let extra_block = region.getExtraBlock(x, y, z)
							let data = block.id;
							if(data!=0)
								str+=getId(data)+"."
							else
								str+="."
							data = extra_block.id;
							if(data!=0)
								str+=getId(data)+"."
							else
								str+="."
							if(x-origin.x!=0)
								str+=x-origin.x+"."
							else
								str+="."
							if(y-origin.y!=0)
								str+=y-origin.y+"."
							else
								str+="."
							if(z-origin.z!=0)
								str+=z-origin.z
							let blockData=[str];
							if(JSON.stringify(block.getNamedStatesScriptable())!="{}")
								blockData.push(block.getNamedStatesScriptable());
							if(JSON.stringify(extra_block.getNamedStatesScriptable())!="{}"){
								if(blockData.length == 1)
									blockData.push({});
								blockData.push(extra_block.getNamedStatesScriptable());
							}
							if(arr[3] == "false"){
								if(block.id != 0){
									stru.push(blockData);
								}
							}else{
								stru.push(blockData);
							}
						}
					}
				}
				FileTools.WriteJSON(__dir__+"output/"+arr[2]+".struct", stru, arr[4] == "true");
				Game.message("Структура сохранена")
			}
		}
	}catch(e){
		Game.message(e);
	}
})