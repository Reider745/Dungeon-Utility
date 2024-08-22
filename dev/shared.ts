ModAPI.registerAPI("DungeonUtility", {
	StructureLoader: StructureLoader,
	Structure: Structure,
	ItemGeneration: ItemGeneration,
	VisualStructure: VisualStructure,
	StructureUtility: StructureUtility,
	StructureRotation: StructureRotationJS,//TODO: В генерирумых декларациях заменять на StructureRotation в ручную 
	BlockData: BlockData,
	StructureIntegration: StructureIntegration,
	StructureDescription: StructureDescription,
	StructurePool: StructurePool,
	StructurePiece: StructurePiece,
	StructureDestructibility: StructureDestructibility,
    DefaultGenerationDescription: DefaultGenerationDescription,
	requireGlobal(command){
		return eval(command);
	},
	getDir(){
		return __dir__;
	},
	version: 5
});

{
    let use_wand = false, firstClick = false;
    let coordinates = [
        {x:0,y:0,z:0},
        {x:0,y:0,z:0}
    ];
    let origin = undefined;

    Callback.addCallback("ItemUseLocal", (coords, item, block, player) => {
        if(use_wand){
            if(item.id == VanillaItemID.wooden_axe && Entity.getSneaking(player)){
                origin = coords;
                Game.message("установлен цент структуры");
                Game.prevent();
            }else if(item.id == VanillaItemID.wooden_axe && !Entity.getSneaking(player)){
                if(!firstClick){
                    coordinates[1] = coords;
                    Game.message("вторая точка");
                }else{
                    Game.message("первая точка");
                    coordinates[0]=coords;
                }
                firstClick = !firstClick;
                Game.prevent();
            }   
        }
    });

    type PacketSet = {
        pool_name: string;
        structure_name: string;
    };

    Network.addServerPacket("dungeonutility.command.set", (client, data: PacketSet) => {
        const playerUid = client.getPlayerUid();

        if(new PlayerActor(playerUid).isOperator() && typeof data.pool_name == "string" && typeof data.structure_name == "string"){
            const pool = StructureLoader.getStructurePoolByName(data.pool_name);
            if(pool){
                const pos = Entity.getPosition(playerUid);
                Structure.setStructure(pool.get(data.structure_name), pos.x, pos.y, pos.z, BlockSource.getDefaultForActor(playerUid));
            }
        }
    });

    const CompatibilityBase = WRAP_JAVA("com.reider.dungeonutility.struct.formats.du_v2.compatibility.CompatibilityBase");

    Callback.addCallback("NativeCommand", (cmd) => {
        let args = cmd.split(" ")
        if(args[0] == "/du"){
            use_wand = !use_wand;
            Game.message("Топор теперь можно использовать для сохранения структур");
            Game.prevent();
            return;
        }
        
        try{
            if(args[0] == "/struct"){
                if(args[1] == "save" && args[2]){
                    Game.prevent();

                    if((coordinates[0].x == 0 && coordinates[0].y == 0 && coordinates[0].z == 0) ||
                        (coordinates[1].x == 0 && coordinates[1].y == 0 && coordinates[1].z == 0)){
                            Game.message(EColor.RED+"ERROR: НЕВОЗМОЖНО СОХРАНИТЬ СТРУКТУРУ, ОДНА ИЗ ТОЧЕК НЕ ВЫБРАНА!");
                            return;
                        }

                    if(!origin)
                        Game.message(EColor.RED+"WARNING: Центр структуры не был установлен!");

                    StructureLoader.setStructure(args[2], new StructureDescriptionJava(StructureUtility.getStructureByPos(coordinates, origin || coordinates[1], args[3] == "true")));
                    StructureLoader.save(__dir__+"output/"+args[2]+".struct", args[2], args[5] || "DungeonUtility", args[4] == "true")

                    if(args[6])//Сжатие структуры с мощью алгоритма хаффмана
                        StructureLoader.compile(__dir__+"output/"+args[2]+".struct");

                    coordinates = [
                        {x:0,y:0,z:0},
                        {x:0,y:0,z:0}
                    ];
                    origin = undefined;

                    Game.message("Структура сохранена, точки сброшены до значений по умолчанию");
                    //FileTools.WriteJSON(__dir__+"assets/cache.json", cache, false);
                }else if(args[1] == "list"){// struct list 
                    const all = StructureLoader.getAllStructureAndPool();
                    const it: java.util.Iterator<string> = all.keySet().iterator();

                    while (it.hasNext()){
                        const pool_name: string = it.next();
                        Game.message(EColor.BLUE+pool_name);

                        const java_list: string[] = all.get(pool_name);
                        for(let i = 0;i < java_list.length;i++)
                            Game.message("- "+java_list[i]);
                    }
                    Game.prevent();
                }else if(args[1] == "set" && args[2]){// struct save structure_name pool_name(optional) 
                    let packet: PacketSet =  {
                        pool_name: args[3] || "default",
                        structure_name: args[2]
                    }
                    Network.sendToServer("dungeonutility.command.set", packet);
                    Game.prevent();
                    Game.message(EColor.GREEN + "Отправлено серверу на обработку");
                }else if(args[1] == "du2"){// Отображает структуру в более человеческом формате
                    const base = new CompatibilityBase(new java.util.HashMap());
                    let buffer = java.nio.ByteBuffer.wrap(Utils.readFileBytes(__dir__+"output/"+args[2]+".struct"));
                    buffer.get()// version
                    base.readZones(buffer);
                    Game.message(String(base.toString()));
                    Utils.writeFileBytes(__dir__+"output/"+args[2 ]+".struct.txt", base.toString().getBytes() as number[]);
                    Game.prevent();
                }
            }
        }catch(e){
            Game.message(e);
        }
    });
}

// EXAMPLES

/*let pool = new StructurePool("test")
    .setPathStructures(__dir__);

pool.upload("test", "DungeonUtility_V2");
pool.upload("test2", "DungeonUtility_V2");

Callback.addCallback("StructureLoadOne", () => {
    new DefaultGenerationDescription(pool.get("test2"), 60)
        .setIdentifier("test:structure")
        .setDistance(140, "test:structure")
        .setGenerationParams(0, 0, 10)
        .register();
});*/

/*Callback.addCallback("StructureLoadOne", () => {
    let pool = new StructurePool("test");

    pool.put("mystructure", new StructureDescription()
        .addBlock(0, 0, 0, new BlockState(VanillaBlockID.stonebrick, 0))
        .addBlock(0, 1, 0, new BlockState(VanillaBlockID.stonebrick, 1))
        .addBlock(0, 2, 0, new BlockState(VanillaBlockID.chest, 0))
        .getDescription());

    ItemGeneration.newGenerator("generationTest");
    ItemGeneration.addItem("generationTest", VanillaItemID.iron_chestplate, .5);
    ItemGeneration.addItem("generationTest", VanillaItemID.diamond, .5);
    ItemGeneration.addItem("generationTest", VanillaItemID.gold_ingot, .5);
        
    pool.setGlobalPrototype("mystructure", Structure.getPrototypeDefault("generationTest"))

    pool.put("wood", new StructureDescription()
        .addBlock(0, 0, 0, new BlockState(VanillaBlockID.log, 0))
        .addBlock(0, 1, 0, new BlockState(VanillaBlockID.log, 0))
        .addBlock(0, 2, 0, new BlockState(VanillaBlockID.leaves, 0))
        .addBlock(0, 3, 0, new BlockState(VanillaBlockID.log, 0))
        .addBlock(0, 4, 0, new BlockState(VanillaBlockID.leaves, 0))
        .getDescription());

    let mystructure = new Structure.advanced(pool.get("mystructure"))
    new DefaultGenerationDescription(mystructure, 1)
        .setGenerationParams(0, 0, 10)
        .setDistance(60, "mystucture")//Расстояние между этой структурой будет минимум 60
        .setSurface(true, [VanillaBlockID.grass, VanillaBlockID.dirt])
        .register()

    let wood = new Structure.advanced(pool.get("wood"))
    new DefaultGenerationDescription(wood, 1)
        .setGenerationParams(0, 0, 2, 0, [1, 2, 3])// 1, 2, 3 - Количество деревьев которые будут спавнится в чанке
        .setStorage(false)// Не сохраняем список структур
        .register();
});*/