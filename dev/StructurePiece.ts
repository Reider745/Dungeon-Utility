const StructurePieceController = WRAP_JAVA("com.reider.dungeonutility.struct.generation.StructurePieceController");
const DefaultDescription = WRAP_JAVA("com.reider.dungeonutility.struct.generation.types.api.DefaultGeneration");
const Vector3 = WRAP_JAVA("com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3");

type GENERATION_TYPE = "default" | "Nether" | "OverWorld";

interface ILegacyDescription {
    type?: GENERATION_TYPE;
    save?: boolean;
    offset?: {x?: number, y?: number, z?: number}
    structure: Structure.advanced;
    name?: string;
    chance: number;
    distance?: number;
    isSet?: boolean;
    dimension?: number;

    white_list?: boolean;
    biomes?: number[];

    white_list_blocks?: boolean;
    blocks?: number[];

    checkName?: boolean;
    optimization?: boolean;
    legacySpawn?: boolean;
    clearToMembory?: number;
    count?: number[];
    minAndMaxY?: [number, number];
    legacy_offset?: boolean;
}

type PacketMessage = {
    text: string;
}

Network.addClientPacket("message", (data: PacketMessage) => {
	Game.message(data.text);
})

Network.addServerPacket("DungeonUtility.optimization", function(client){
	const player = client.getPlayerUid();

	if(new PlayerActor(player).isOperator()){
		const pos = Entity.getPosition(player);
        const client = Network.getClientForPlayer(player);
        const packet: PacketMessage = {
            text: "Вами была произведена принудительная оптимизация структур."
        };

        StructurePieceController.algorithms.algorithmsOptimization(new Vector3(pos.x, pos.y, pos.z));
		
		client && client.send("message", packet);
	}else{
		const client = Network.getClientForPlayer(player);
        const packet: PacketMessage = {
            text: "У вас нет доступа к очистке"
        };

		client && client.send("message", packet);
	}
});

Callback.addCallback("NativeCommand", (cmd) => {
	if(cmd == "/optimization")
		Network.sendToServer("DungeonUtility.optimization", {});
});

let listPiece: IGenerationDescription[] = [];
let reg = false;

Callback.addCallback("StructureLoad", function(){
	if(reg)
		return;
	reg = true;
	Callback.invokeCallback("StructureLoadOne");
	for(let i in listPiece){
		let pieces = StructurePieceController.getPieces();
		for(let a in pieces)
			StructurePieceController.getPiece(pieces[a])
                .addGeneration(listPiece[i]);
	}
});

class DefaultGenerationDescription {
    private readonly structure: Structure.advanced;
    private readonly chance: number;
    private readonly type: GENERATION_TYPE;

    constructor(structure: Structure.advanced, chance: number, type: GENERATION_TYPE = "default"){
        this.structure = structure;
        this.chance = chance;
        this.type = type;

        this.setGenerationParams();
        this.setDistance();
        this.setStorage();
        this.setConditionsSpawned();
        this.setBiomes();
        this.setSurface();
    }

    private dimension: number;
    private offset: Vector;
    private count: number[];
    public setGenerationParams(dimension: number = 0, x: number = 0, y: number = 0, z: number = 0, count: number[] = [1]): DefaultGenerationDescription {
        this.dimension= dimension;
        this.offset = {x, y, z};
        this.count = count;
        return this;
    }

    private distance: number;
    private name: string;
    public setDistance(distance: number = 0, name: string = null): DefaultGenerationDescription {
        this.distance = distance;
        this.name = name;
        return this;
    }

    private storage_structure: boolean;
    private queue_clear: boolean;
    private storage_queue: number;
    public setStorage(storage_structure: boolean = true, queue_clear: boolean = true, storageQueue: number = 60000): DefaultGenerationDescription {
        this.storage_structure = storage_structure;
        this.queue_clear = queue_clear;
        this.storage_queue = storageQueue;
        return this;
    }

    private check_place: boolean;
    private min_y: number;
    private max_y: number
    public setConditionsSpawned(check_place: boolean = false, min_y: number = 0, max_y: number = 256): DefaultGenerationDescription {
        this.check_place = check_place;
        this.min_y = min_y;
        this.max_y = max_y;
        return this;
    }

    private biomes_white_list: boolean;
    private biomes: number[];
    public setBiomes(white_list: boolean = false, list: number[] = []): DefaultGenerationDescription {
        this.biomes_white_list = white_list;
        this.biomes = list;
        return this;
    }

    private surface_white_list: boolean;
    private surface: number[];
    /**
     * Рекомендую не использовать, плохая совместимость с серверным ядром на основе Nukkit-Mot
     * @param white_list - белый список или черный списое
     * @param list - список
     * @returns - возвращает самого себя
     */
    public setSurface(white_list: boolean = false, list: number[] = []): DefaultGenerationDescription {
        this.surface_white_list = white_list;
        this.surface = list;
        return this;
    }

    public clone(): DefaultGenerationDescription {
        return new DefaultGenerationDescription(this.structure, this.chance, this.type)
            .setGenerationParams(this.dimension, this.offset.x, this.offset.y, this.offset.z, this.count)
            .setDistance(this.distance, this.name)
            .setStorage(this.storage_structure, this.queue_clear, this.storage_queue)
            .setConditionsSpawned(this.check_place, this.min_y, this.max_y)
            .setBiomes(this.biomes_white_list, this.biomes)
            .setSurface(this.surface_white_list, this.surface);
    }

    public build(): IGenerationDescription {
        return StructurePiece.getDefault({
            structure: this.structure,
            chance: this.chance,
            type: this.type,

            //legacy
            legacy_offset: false,
            legacySpawn: false,

            //distance
            distance: this.distance,
            checkName: !!this.name,
            name: this.name,

            //generation params
            dimension: this.dimension,
            offset: this.offset,
            count: this.count,

            //storage params
            save: this.storage_structure,
            optimization: this.queue_clear,
            clearToMembory: this.storage_queue,

            //conditions spawned
            isSet: this.check_place,
            minAndMaxY: [this.min_y, this.max_y],

            //biomes
            white_list: this.biomes_white_list,
            biomes: this.biomes,

            //surface
            white_list_blocks: this.surface_white_list,
            blocks: this.surface
        });
    }

    public register(): DefaultGenerationDescription {
        StructurePiece.register(this.build());
        return this;
    }
}

namespace StructurePiece {
    export function registerType(cl: IGenerationType): void {
        let pieces = StructurePieceController.getPieces();
		for(let i in pieces)
			StructurePieceController.getPiece(pieces[i]).registerType(cl);
    }

    /**@deprecated */
    export function getDefault(obj: ILegacyDescription): IGenerationDescription {
        obj.save = obj.save === undefined ? true :  obj.save;
		obj.offset = obj.offset || {};
		if(obj.structure)
			return new DefaultDescription(obj.type || "default", obj.name || "noy_name", obj.offset.x||0, obj.offset.y||0, obj.offset.z||0, 
                                        Number(obj.chance)||50, obj.distance || 0, !!obj.save, !!obj.isSet, obj.dimension || 0, 
                                        !!obj.white_list, obj.biomes || [], !!obj.white_list_blocks, obj.blocks || [0], obj.structure.getStructureJava(), 
                                        !!obj.checkName, obj.optimization === undefined ? true : obj.optimization, !!obj.legacySpawn, 
                                        obj.clearToMembory || 60000, obj.count||[1], obj.minAndMaxY||[0, 255], 
                                        obj.legacy_offset === undefined ? true : obj.legacy_offset);
		else{
			Logger.Log("Error StructurePiece register, Structure = undefined or null "+obj.name || "noy_name", "DungeonUtility");
			return null;
		}
    }

    export function generateStructure(stru: IGenerationDescription, x: number, y: number, z: number, random: java.util.Random, region: BlockSource, packet?: any): void {
		StructurePieceController.getPiece()
            .spawnStructure(stru, new Vector3(x, y, z), region, packet || {}, random, region.getDimension());
	}

    export function register(stru: IGenerationDescription): void {
		listPiece.push(stru);
	}

	export function getNearestStructure(x: number, y: number, z: number, region: BlockSource, name?: string, checkName?: boolean): JavaWorldStructure{
		return StructurePieceController.getStorage()
            .getNearestStructure(new Vector3(x, y, z), region.getDimension(), name || null, !!checkName);
	}

	export function addStructure(name: string, x: number, y: number, z: number, region: BlockSource): void {
		StructurePieceController.getStorage()
            .add(name, x, y, z, region);
	}

	export function deleteStructure(x: number, y: number, z: number): void {
		StructurePieceController.getStorage()
            .del(x, y, z);
	}
};