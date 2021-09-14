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

StructureLoader.registerType("DungeonAPI", {
	read(file){
		let arr = [];
		let stru = file.split(":");
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(new BlockData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : BlockID[data[0]], parseInt(data[1])), new BlockState(0, {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	save(stru){
		let str = "";
		for(let i in stru){
			str += getId(stru[i].state.id)+"."+stru[i].state.data+"."+stru[i].x+"."+stru[i].y+"."+stru[i].z;
			if(i == stru.length - 1)
			 str += ":";
		}
		return str;
	}
})

StructureLoader.registerType("DungeonAPI_V2", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(new BlockData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : BlockID[data[0]], parseInt(data[1])), new BlockState(0, {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	save(stru){
		let arr = [];
		for(let i in stru){
			arr.push(getId(stru[i].state.id)+"."+stru[i].state.data+"."+stru[i].x+"."+stru[i].y+"."+stru[i].z)
		}
		return JSON.stringify(arr);
	}
})

StructureLoader.registerType("DungeonCore", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			stru[i][3] = stru[i][3] || [];
			stru[i][3][0] = stru[i][3][0] || 0;
			stru[i][3][1] = stru[i][3][1] || {};
			arr.push(new BlockData(parseInt(stru[i][1].split(".")[1]), parseInt(stru[i][1].split(".")[2]), parseInt(stru[i][1].split(".")[3]), new BlockState(typeof(stru[i][0]) == "string" ? BlockID[stru[i][0]] : stru[i][0], stru[i][2]), new BlockState(stru[i][3][0] == "string" ? BlockID[stru[i][3][0]] : stru[i][3][0], stru[i][3][1]), new NBT.CompoundTag()))
		}
		return arr;
	},
	save(stru){
		let arr = [];
		for(let i in stru){
			let block = [getId(stru[i].state.id), stru[i].state.data+"."+stru[i].x+"."+stru[i].y+"."+stru[i].z, stru[i].state.getNamedStatesScriptable(), [getId(stru[i].stateExtra.id), stru[i].stateExtra.getNamedStatesScriptable()]];
			arr.push(block);
		}
		return JSON.stringify(arr);
	}
});

StructureLoader.registerType("Structures", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file).structure;
		for(let i in stru){
			arr.push(new BlockData(stru[i][0], stru[i][1], stru[i][2], new BlockState(typeof(stru[i][3]) == "string" ? BlockID[stru[i][3]] : (typeof(stru[i][3]) == "object" ? stru[i][3].id : 0), stru[i][3].id ? stru[i][3].data : 0), new BlockState(0, {}), new NBT.CompoundTag()));
		}
		return arr;
	},
	save(stru){
		let arr = {
			version: 3,
			structure: []
		};
		for(let i in stru){
			arr.structure.push([stru[i].x, stru[i].y, stru[i].z, {id: getId(stru[i].state.id)},null])
		}
		return JSON.stringify(arr);
	}
});

StructureLoader.registerType("DungeonUtility", {
	read(file){
		let arr = [];
		let stru = JSON.parse(file);
		for(let i in stru){
			let data = stru[i][0].split(".")
			arr.push(new BlockData(parseInt(data[2])||0, parseInt(data[3])||0, parseInt(data[4])||0, new BlockState(typeof(parseInt(data[0])||(data[0]==""?0:data[0])) == "number" ? parseInt(data[0])||0 : BlockID[data[0]], stru[i][1] || {}), new BlockState(typeof(parseInt(data[1])||(data[1]==""?0:data[1])) == "number" ? parseInt(data[1])||0 : BlockID[data[1]], stru[i][2] || {}), new NBT.CompoundTag()))
		}
		return arr;
	},
	save(stru){
		let arr = []
		for(let i in stru){
			let str = "";
			let data = stru[i].state.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			data = stru[i].stateExtra.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			if(stru[i].x!=0)
				str+=stru[i].x+"."
			else
				str+="."
			if(stru[i].y!=0)
				str+=stru[i].y+"."
			else
				str+="."
			if(stru[i].z!=0)
				str+=stru[i].z
			let blockData=[str];
			arr.push(blockData)
			if(JSON.stringify(stru[i].state.getNamedStatesScriptable())!="{}")
				blockData.push(stru[i].state.getNamedStatesScriptable());
			if(JSON.stringify(stru[i].stateExtra.getNamedStatesScriptable())!="{}"){
				if(blockData.length == 1)
					blockData.push({});
				blockData.push(stru[i].stateExtra.getNamedStatesScriptable());
			}
			arr.push(blockData)
		}
		return JSON.stringify(arr);
	}
});