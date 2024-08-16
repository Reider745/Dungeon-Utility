function StructureDescription(stru_name){
	if(stru_name == undefined || stru_name == null)
		var stru = new StructureDescriptionJava([]);
	else
		var stru = StructureLoader.getStructure(stru_name);
	let cache = false;
	let blocks = {};
	
	//включает кэширование
	this.cacheEnable = function(value){
		cache = value;
		return this;
	}
	
	//обновлет структуру
	this.cacheUpdate = function(){
		let arr = [];
		let keys = Object.keys(blocks);
		for(let i in keys){
			let pos = keys[i].split(":");
			arr.push(BlockData.createData(parseInt(pos[0])||0, parseInt(pos[1])||0, parseInt(pos[2])||0, blocks[keys[i]]));
		}
		stru.blocks = arr;
		return this;
	}
	
	//проверяет включёноли кэширование
	this.isCache = function(){
		return cache;
	}
	
	//добавляет блок в структуру 
	this.addBlock = function(x, y, z, state){
		blocks[x+":"+y+":"+z] = state;
		return this;
	}
	
	//возвращает блок
	this.getBlock = function(x, y, z){
		return blocks[x+":"+y+":"+z];
	}
	
	//проверяет естли блок
	this.isBlock = function(x, y, z){
		return !!this.getBlock(x, y, z);
	}
	
	//возвращает java описание структуры 
	this.getDescription = function(){
		if(!cache)
			this.cacheUpdate();
		return stru;
	}
	
	this.save = function(name){
		if(!cache)
			this.cacheUpdate();
		StructureLoader.setStructure(name, stru);
		return this;
	}
	
	this.getBlocks = function(){
		return stru.blocks;
	}
	this.setBlocks = function(blocks){
		stru.blocks = blocks;
		return this;
	}
}

/*//создаём экземпляр StructureDescription
let Test = new StructureDescriptionJS();

//добавляем блок(относительно центра)
Test.addBlock(0, 0, 0, new BlockState(1, 0));
Test.addBlock(0, 1, 0, new BlockState(54, 0));
Test.addBlock(0, 2, 0, new BlockState(1, 0));

Test.cacheEnable(true);//отключаем обновление блоков у getDescription и save(для более быстрой работы)
Test.cacheUpdate();//обновляем блоки
Test.save("test");//сохраняем структуру под именем test

//устанавливаем структуру, при нажатии 
Callback.addCallback("ItemUse", function(pos){
	//любой метод место имени структуры может принять Test.getDescription()
	Structure.set(Test.getDescription(), pos.x, pos.y+1, pos.z);
	Structure.set("test", pos.x, pos.y+4, pos.z);
});*/