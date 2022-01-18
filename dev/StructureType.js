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
function getState(id, state){
	/*let block_state = new BlockState(id, state);
	if(JSON.stringify(StructureUtility.getStateByData(id, block_state.data))==JSON.stringify(state))
		return block_state.data;*/
	return state;
}

StructureLoader.registerType("DungeonAPI", {
	read(file){
		let arr = [];
		let stru = file.split(":");
		for(let i in stru){
			let data = stru[i].split(".");
			arr.push(BlockData.createData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(Block.convertBlockToItemId(parseInt(data[0]) ? parseInt(data[0]) : data[0] == "0" ? 0 : BlockID[data[0]]), parseInt(data[1]))))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let str = "";
		let stru = struct.blocks;
		for(let i in stru){
			let data = stru[i].getData();
			str += getId(data.state.id)+"."+data.state.data+"."+data.x+"."+data.y+"."+data.z;
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
			arr.push(BlockData.createData(parseInt(data[2]), parseInt(data[3]), parseInt(data[4]), new BlockState(parseInt(data[0]) ? parseInt(data[0]) : data[0] == "0" ? 0 : BlockID[data[0]], parseInt(data[1]))))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = [];
		for(let i in stru){
			 data = stru[i].getData();
			arr.push(getId(data.state.id)+"."+data.state.data+"."+data.x+"."+data.y+"."+data.z)
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
			arr.push(BlockData.createData(stru[i][1].split(".")[1] == "0" ? 0 : parseInt(stru[i][1].split(".")[1]), stru[i][1].split(".")[2] == "0" ? 0 : parseInt(stru[i][1].split(".")[2]), stru[i][1].split(".")[3] == "0" ? 0 : parseInt(stru[i][1].split(".")[3]), new BlockState(typeof(stru[i][0]) == "string" ? BlockID[stru[i][0]] : stru[i][0], stru[i][2]), new BlockState(stru[i][3][0] == "string" ? BlockID[stru[i][3][0]] : stru[i][3][0], stru[i][3][1])))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = [];
		for(let i in stru){
			 data = stru[i].getData();
			let block = [getId(data.state.id), data.state.data+"."+data.x+"."+data.y+"."+data.z, data.state.getNamedStatesScriptable(), [getId(data.stateExtra.id), data.stateExtra.getNamedStatesScriptable()]];
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
			arr.push(BlockData.createData(stru[i][0], stru[i][1], stru[i][2], new BlockState(typeof(stru[i][3]) == "string" ? BlockID[stru[i][3]] : (typeof(stru[i][3]) == "object" ? stru[i][3].id : 0), stru[i][3].id ? stru[i][3].data : 0)));
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = {
			version: 3,
			structure: []
		};
		for(let i in stru){
			data = stru[i].getData();
			arr.structure.push([data.x, data.y, data.z, {id: getId(data.state.id), data: data.state.data},null])
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
			arr.push(BlockData.createData(parseInt(data[2])||0, parseInt(data[3])||0, parseInt(data[4])||0, new BlockState(typeof(parseInt(data[0])||(data[0]==""?0:data[0])) == "number" ? parseInt(data[0])||0 : BlockID[data[0]], typeof(stru[i][1] || {})=="number" ? stru[i][1] == "0" ? 0 : parseInt(stru[i][1]) : stru[i][1] || {}), new BlockState(typeof(parseInt(data[1])||(data[1]==""?0:data[1])) == "number" ? parseInt(data[1])||0 : BlockID[data[1]], typeof(stru[i][2] || {})=="number" ? stru[i][2] == "0" ? 0 : parseInt(stru[i][2]) : stru[i][2] || {}), null))
		}
		return (new StructureDescription(arr));
	},
	save(struct){
		let stru = struct.blocks;
		let arr = []
		for(let i in stru){
			let str = "";
			let b = stru[i].getData();
			let data = b.state.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			data = b.stateExtra.id;
			if(data!=0)
				str+=getId(data)+"."
			else
				str+="."
			if(b.x!=0)
				str+=b.x+"."
			else
				str+="."
			if(b.y!=0)
				str+=b.y+"."
			else
				str+="."
			if(b.z!=0)
				str+=b.z
			let blockData=[str];
			if(JSON.stringify(b.state.getNamedStatesScriptable())!="{}"||getState(b.state.id,b.state.getNamedStatesScriptable())!=0)
				blockData.push(getState(b.state.id,b.state.getNamedStatesScriptable()));
			if(JSON.stringify(b.stateExtra.getNamedStatesScriptable())!="{}"||getState(b.stateExtra.id,b.stateExtra.getNamedStatesScriptable())!=0){
				if(blockData.length == 1)
					blockData.push(0);
				blockData.push(getState(b.stateExtra.id,b.stateExtra.getNamedStatesScriptable()));
			}
			arr.push(blockData)
		}
		return JSON.stringify(arr);
	}
});
