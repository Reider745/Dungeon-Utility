let StructureCompileJava = WRAP_JAVA("com.reider.dungeon_utility.struct.formats.StructureCompile");
let DungeonUtilityCompile = WRAP_JAVA("com.reider.dungeon_utility.struct.formats.DungeonUtilityFormat");
let CompileData = WRAP_JAVA("com.reider.dungeon_utility.struct.formats.SymbolData");

let StructureCompile = {
	register(name, format){
		StructureCompileJava.register(name, format);
	},
	getFormat(name){
		return StructureCompileJava.getFormat(name);
	},
	decompile(name, arr){
		return this.getFormat(name).getStringByBytes(arr);
	},
	compile(name, str){
		return this.getFormat(name).getBytesByString(str);
	},
	writeFile(name, arr){
		StructureCompileJava.writeFile(name, arr);
	},
	getCompileDataByObject(obj){
		let arr = [];
		let keys = Object.keys(obj);
		for(let i in keys)
			arr.push(new CompileData(keys[i], obj[keys[i]]));
		return arr;
	}
};

StructureCompile.register("DungeonUtility", new DungeonUtilityCompile(0, StructureCompile.getCompileDataByObject({
	" ": 0,
	".": 1,
	"-": 2,
	"1": 3,
	"2": 4,
	"3": 5,
	"4": 6,
	"5": 7,
	"6": 8,
	"7": 9,
	"8": 11,
	"9": 12,
	"0": 13,
})));

StructureCompile.writeFile(__dir__+"ggg.txt", StructureCompile.compile("DungeonUtility", "25.11.21"));
let bytes = new java.lang.String(FileTools.ReadText(__dir__+"ggg.txt")).getBytes()
alert(StructureCompile.decompile("DungeonUtility", bytes));