const StructureJava = WRAP_JAVA("com.reider.dungeonutility.struct.Structure");
const DefaultStructurePrototype = WRAP_JAVA("com.reider.dungeonutility.struct.prototypes.DefaultStructurePrototype");
const StructureDestructibilityJava = WRAP_JAVA("com.reider.dungeonutility.struct.prototypes.StructureDestructibility");

function getRegion(): BlockSource {
    return BlockSource.getCurrentWorldGenRegion();
};

class StructureDestructibility {
    private readonly self = new StructureDestructibilityJava();

    public addBlock(id: number, state: BlockState): StructureDestructibility {
        this.self.addBlock(id, state);
        return this;
    }

    public get(): JavaStructureDestructibility {
        return this.self;
    }
}

interface ILegacyDefaultStructurePrototype {
    name?: string,
    blocks: JavaStructureDestructibility
}

namespace Structure {
    export function getPrototypeDefault(obj: string | ILegacyDefaultStructurePrototype, blocks?: StructureDestructibility){
        if(typeof obj == "string")
            return new DefaultStructurePrototype(obj, (blocks || new StructureDestructibility()).get());
		return new DefaultStructurePrototype(obj.name ? obj.name : null, obj.blocks);
	}

    export function setStructure(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource, packet?: any): void {
		StructureJava.setStructure(StructureLoader.getStructure(name), x, y,z, region||getRegion(), packet||{});
	}

    export function set(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource, packet?: any): void {
		setStructure(name, x, y,z, region, packet);
	}

    export function build(name: STRUCTURE, x: number, y: number, z: number, sleep: number, region?: BlockSource, packet?: any): void {
		StructureJava.build(StructureLoader.getStructure(name), x, y,z, region||getRegion(), sleep, packet||{});
	}

    export function isStructure(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): boolean {
		return StructureJava.isStructure(StructureLoader.getStructure(name), x, y, z, region||getRegion());
	}

    export function is(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): boolean {
        return isStructure(name, x, y, z, region);
	}

    export function isSetStructure(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): boolean {
		return StructureJava.isStructure(StructureLoader.getStructure(name), x, y, z, region||getRegion());
	}

    export function isSet(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): boolean {
        return isSetStructure(name, x, y, z, region);
	}

    export function canSet(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): boolean {
        return isSetStructure(name, x, y, z, region);
	}

    export function destroy(name: STRUCTURE, x: number, y: number, z: number, region?: BlockSource): void {
		StructureJava.destroy(StructureLoader.getStructure(name), x||0, y||0, z||0, region||getRegion());
	}

    export class advanced {
        public readonly stru: JavaStructure;

        constructor(name: STRUCTURE){
            if(name instanceof StructureDescriptionJava)
                this.stru = new StructureJava(name);
            else{
                this.stru = new StructureJava(new StructureDescriptionJava([]));
                if(StructureLoader.isLoad(name))
                    this.stru.setStructure(StructureLoader.getStructure(name));
                else{
                    let self = this;
                    Callback.addCallback("StructureLoad", () => self.stru.setStructure(StructureLoader.getStructure(name)));
                }
            }
        }

        public getStructureJava(): JavaStructure {
			return this.stru;
		}

        public setUseGlobalPrototype(value: boolean): advanced {
            this.stru.setUseGlobalPrototype(value);
            return this;
        }

        public isUseGlobalPrototype(): boolean {
			return this.stru.isUseGlobalPrototype();
		}

        public setProt(obj: IStructurePrototype): advanced{
			try{
				obj.isBlock = obj.isBlock || function(){return true};
			}catch(e){}
			this.stru.setPrototype(obj);
			return this;
		}

        public getPrototype(): IStructurePrototype {
            return this.stru.getPrototype();
        }

        public getStructure(): JavaStructureDescription {
			return this.stru.getStructure();
		}

        public isStructure(x: number, y: number, z: number, region?: BlockSource): boolean {
			return this.stru.isStructure(x, y, z, region||getRegion());
		}

        public isSetStructure(x: number, y: number, z: number, region?: BlockSource): boolean {
			return this.stru.isStructure(x, y, z, region||getRegion());
		}

        public is(x: number, y: number, z: number, region?: BlockSource): boolean {
            return this.isStructure(x, y, z, region);
        }
    
        public canSet(x: number, y: number, z: number, region?: BlockSource): boolean {
            return this.isSetStructure(x, y, z, region);
        }

        public setStructure(x: number, y: number, z: number, region?: BlockSource, packet?: any): advanced {
			this.stru.setStructure(x, y, z, region||getRegion(), packet||{});
            return this;
		}

        public set(x: number, y: number, z: number, region?: BlockSource, packet?: any): void {
            this.setStructure(x, y,z, region, packet);
        }

        public build(x: number, y: number, z: number, sleep: number, region: BlockSource, packet: any): advanced{
            let self = this;
			Threading.initThread("Structure-build", () =>
				self.stru.build(x, y, z, region||getRegion(), sleep, packet||{}));
			return this;
		}

		public destroy(x: number, y: number, z: number, region?: BlockSource): advanced {
			this.stru.destroy(x, y, z, region||getRegion());
			return this;
		}
        
        /** @deprecated */
        public setStruct(name: string): advanced {
			this.stru.setStructure(new StructureDescriptionJava(Structure.getStructure(name)));
			return this;
		}

        /** @deprecated */
        public setPrototype(obj: any): advanced {
			obj.isBlock = obj.isBlock || function(){return true};
			const funcIsBlock = obj.isBlock;
			obj.isBlock = function(original_pos, data, region, packet){
				return funcIsBlock(original_pos, {
					x: original_pos.x + data.x,
					y: original_pos.y + data.y,
					z: original_pos.z + data.z
				}, data.state, data.stateExtra, data, region, packet);
			}
			const funcSetBlock = obj.setBlock;
			if(funcSetBlock)
				obj.setBlock = function(original_pos, data, region, packet){
					funcSetBlock(original_pos, {
						x: original_pos.x + data.x,
						y: original_pos.y + data.y,
						z: original_pos.z + data.z
					}, data.state, data.stateExtra, data, region, packet);
			}
			this.stru.setPrototype(obj);
			return this;
		}
    }

    /** @deprecated */
    export function getRandomCoords(x: number, z: number, random: java.util.Random, obj?: {max?: number, min?: number}): Vector {
		obj = obj || {}
		return GenerationUtils.findSurface(x*16 + random.nextInt(16), random.nextInt((obj.max||100) - (obj.min||50)) + (obj.min||50), z*16 + random.nextInt(16));
	}

    /** @deprecated */
    export function setGlobalPrototype(name: string, obj: IStructurePrototype){
        try{
            obj.isBlock = obj.isBlock || function(){return true};
        }catch(e){}
        try {
			StructureJava.setGlobalPrototype(name, obj);
			return;
		} catch (error) {}
		try{
			Callback.addCallback("StructureLoad", function(){
				StructureJava.setGlobalPrototype(name, obj);
			});
		}catch(e){
			StructureJava.setGlobalPrototype(name, obj);
		}
	}

    /** @deprecated */
    export function getGlobalPrototype(name: string){
		return StructureJava.getGlobalPrototype(name);
	}

    /** @deprecated */
    export function getStructure(name: string): JavaBlockData[]{
		if(!Array.isArray(name))
			return StructureLoader.getStructure(name).blocks;
		return [];
	}

    /** @deprecated */
    export function addFeatureHandler(): void {}
    
    /*
    Супер устравшие методы генерации, вырезаны
    */

    /** @deprecated */
    export function setStructureGeneration(): void {};
    /** @deprecated */
    export function getStructureGeneration(): void {};

    /** @deprecated */
    export namespace GenerateType {
        /** @deprecated */
        export class OverworldFind {}
        /** @deprecated */
        export class CustomDimensionFind {}
    }
}