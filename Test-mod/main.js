ModAPI.addAPICallback("DungeonUtility", function(api){
	const StructureLoader = api.StructureLoader;
	const Structure = api.Structure;
	const StructureUtility = api.StructureUtility;
	const ItemGeneration = api.ItemGeneration;
	//загрузка структуры, 3 параметр формат структуры, по умолчанию DungeonUtility
	//test - имя, по которому будет доступна структура
	StructureLoader.load(__dir__+"struct/test.struct", "test");
	
	Callback.addCallback("ItemUse", function(coords, item, block, isExter, player){
		//устанавливаем структуру, если нажали алмазом
		if(item.id == 264)
			Structure.setStructure("test", coords.x, coords.y, coords.z, BlockSource.getDefaultForActor(player))
	});
	
	//создаём генератор предметов 
	ItemGeneration.newGenerator("test");
	//добавляем генерацию алмаза
	ItemGeneration.addItem("test", 264, 1)
 ItemGeneration.addItem("test", 265, .5)
	
	let Test = new Structure.advanced("test");
	let Test2 = new Structure.advanced();
	Test2.setPrototype({
		isBlock(original_pos, pos, state, extra_state, block, region, packet){
			//не ставим блоки, если позиция блока в структуре по x == 0
			if(block.x == 0)
				return false;
			return true;
		},
		setBlock(original_pos, pos, state, extra_state, block, region, packet){
			//заполняем сундуки
			if(state.id == 54)
				ItemGeneration.fill("test", pos.x, pos.y, pos.z, new java.util.Random(), region)
		},
		before(x, y, z, region, packet){
			Game.message("start")
		},
		after(x, y, z, region, packet){
			Game.message("end")
		}
	});
	Test.setPrototype(Test2.getPrototype());
	
	Callback.addCallback("ItemUse", function(coords, item, block, isExter, player){
		//устанавливаем структуру, если нажали железом
		if(item.id == 265)
			Test.setStructure(coords.x, coords.y, coords.z, BlockSource.getDefaultForActor(player))
	});
	
	Callback.addCallback("StructureLoad", function(){
		//добавляем сундук в структуру, это нужно делать после загрузки структуры
		StructureUtility.addBlock("test", 1, 10, 0, new BlockState(54, 0));
		//обновляем структура 
		Test.setStruct("test")
		//добавляем генерацию структуры
		let GenerateStructure = new Structure.GenerateType.OverworldFind({
			chance: 50,
			stru: Test
		});
		GenerateStructure.count = 2;
		GenerateStructure.update();
	});
});
