const StructureLoaderJava = WRAP_JAVA("com.reider.dungeonutility.struct.loaders.StructureLoader");
const StructurePoolJava = WRAP_JAVA("com.reider.dungeonutility.struct.loaders.StructurePool");
const BlockData = WRAP_JAVA("com.reider.dungeonutility.api.data.BlockData");
const StructureDescriptionJava = WRAP_JAVA("com.reider.dungeonutility.api.StructureDescription");
const StructureCompression = WRAP_JAVA("com.reider.dungeonutility.struct.formats.StructureCompression");
const LoaderType = WRAP_JAVA("com.reider.dungeonutility.struct.formats.LoaderType");
const Utils = WRAP_JAVA("com.reider.dungeonutility.api.Utils");

const DEFAULT_POOL_NAME = "default";
type STRUCTURE = string | JavaStructureDescription;

class StructurePool {
    private readonly self: JavaStructurePool;

    constructor(name: string | JavaStructurePool, global: boolean = true){
        if(name instanceof StructurePoolJava)
            this.self = name;
        else 
            this.self = new StructurePoolJava(name, global);
    }

    public getName(): string {
        return this.self.getName();
    }

    public put(name: string, stru: JavaStructureDescription): StructurePool {
		this.self.setStructure(name, stru);
		return this;
	}

	public setPathStructures(path: string): StructurePool {
		this.self.setPathStructures(path);
		return this;
	}

	public get(name: string): JavaStructureDescription{
		return this.self.getStructure(name);
	}

	public isLoad(name: string): boolean {
		return this.self.isLoad(name);
	}

	public deLoad(name: string ): StructurePool {
		this.self.deLoad(name);
		return this;
	}

	public getAllStructure(): string[] {
		return this.self.getAllStructure();
	}

	public load(path: string, name?: string, type?: FILE_FORMATS, compression?: boolean): StructurePool {
		this.self.load(name, path||"", type||"DungeonUtility", !!compression);
		return this;
	}

	public copy(name1: string, name2: string, prot: IStructureCopy): StructurePool {
		prot = prot || {copyBlock(data){return data}, copyPrototype(prot){return prot}};
		prot.copyBlock = prot.copyBlock || function(data){
			return data;
		}
		prot.copyPrototype = prot.copyPrototype || function(prot){
			return prot;
		}
		this.self.copy(name1, name2, prot);
        return this;
	}

	public StructureAdvanced(name: string): Structure.advanced {
		return new Structure.advanced(this.get(name));
	}

	public registerRotations(stru: STRUCTURE, rotates: ROTATION[]): StructurePool {
		rotates = rotates || StructureRotation.getAllY();
		StructureUtilityJava.registerRotations(StructureLoader.getStructure(stru), stru, rotates);
        return this;
	}

	public setGlobalPrototype(name: string, obj: IStructurePrototype): StructurePool {
		try{
			obj.isBlock = obj.isBlock || function(){return true};
		}catch(e){}
		try {
			this.self.setGlobalPrototype(name, obj);
			return;
		} catch (error) {}
		try{
            let self = this;
			Callback.addCallback("StructureLoad", () => self.self.setGlobalPrototype(name, obj));
		}catch(e){
			this.self.setGlobalPrototype(name, obj);
		}
        return this;
	}

    public loadRuntime(name: string, path: string, type?: FILE_FORMATS, compression?: boolean): StructurePool {
		this.self.loadRuntime(name, path, type || "DungeonUtility", !!compression);
		return this;
	}
}

namespace StructureLoader {
    export function getStructurePoolByName(name: string): StructurePool {
		return new StructurePool(StructureLoaderJava.getStructurePool(name||DEFAULT_POOL_NAME));
	}

	export function getAllPool(): string[] {
		return StructureLoaderJava.getAllPool();
	}

	export function getAllStructureAndPool(): java.util.HashMap<string, string[]>{
		return StructureLoaderJava.getAllStructureAndPool();
	}

	export function registerType(name: string, obj: ILoaderType): void {
		obj.isLoadRuntime = obj.isLoadRuntime || function(){return false;}
		LoaderType.registerType(name, obj);
	}

    export function getStructure(name: STRUCTURE): JavaStructureDescription {
		if(name instanceof StructureDescriptionJava)
			return name;
    
		if(this.isLoad(name||"error"))
			return StructureLoaderJava.getStructure(name||"error");

		Logger.Log("structure noy load "+name, "DungeonUtility");
		alert("error "+name);

		return new StructureDescriptionJava([]);
	}

    export function compile(path: string): void {
		StructureCompression.compression(path, FileTools.ReadText(path))
	}

	export function decompile(path: string): void {
		FileTools.WriteText(path, StructureCompression.decompression(path), false);
	}

    /**@deprecated */
	export function save(path: string, name: string, type?: string, compression?: boolean): void {
		try{
			Utils.writeFileBytes(path, LoaderType.getType(type||"DungeonUtility")
				.save(StructureLoader.getStructure(name)));
			if(compression)
				StructureLoader.compile(path);
		}catch(error){
			Logger.Log("error convert "+error, "DungeonUtility")
		}
	}

    /**@deprecated */
	export function load(path: string, name?: string, type?: FILE_FORMATS, compression?: boolean): void {
		StructureLoaderJava.load(name, path, type||"DungeonUtility", !!compression);
	}

    /**@deprecated */
	export function loadRuntime(path: string, name?: string, type?: FILE_FORMATS, compression?: boolean): void {
		type = type||"DungeonUtility";
		try{
			let start = new Date().getTime();
			if(FileTools.isExists(path))
				StructureLoaderJava.loadRuntime(name, path, type, !!compression);
			else if(__config__.get("debug.info_load"))
				Logger.Log("error path, load structure: "+name, "DungeonUtility");
			if(__config__.get("debug.info_load"))
				Logger.Log("load: "+name+", type: "+type+", time: "+((new Date().getTime())-start), "DungeonUtility");
		}catch(e){
			if(__config__.get("debug.message_error_load"))
				Logger.Log("error load structure: "+name+"\n"+e, "DungeonUtility");
		}
	}

    /**@deprecated */
	export function setStructure(name: string, stru: JavaStructureDescription){
        StructureLoaderJava.getStructurePool(DEFAULT_POOL_NAME)
            .setStructure(name, stru);
	}

    /**@deprecated */
	export function isLoad(name: string): boolean {
		return StructureLoaderJava.isStructureLoad(name);
	}

    /**@deprecated */
	export function deLoad(name: string): void {
		StructureLoaderJava.deLoad(name||"error");
	}
}