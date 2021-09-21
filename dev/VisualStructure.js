let VisualStructure = {
	getStructureModel(name){
		let model = BlockRenderer.createModel();
		let stru = Structure.getStructure(name||[]);
		for(let i in stru){
			model.addBox(stru[i].x, stru[i].y, stru[i].z, stru[i].x+1, stru[i].y+1, stru[i].z+1,  stru[i].state.id, stru[i].state.data)
		}
		return model
	},
	getStructureBitmap(name, size, size_block){
		size = size || 1024;
		size_block = size_block || size;
		let stru = Structure.getStructure(name||[]);
		let boxes = [];
		for(let i in stru){
			boxes.push(new com.zhekasmirnov.innercore.api.mod.ui.GuiBlockModel.Builder.PrecompiledBox(null, stru[i].x, stru[i].y, 1.0 - stru[i].z, stru[i].x+1, stru[i].y+1, 1.0 - (stru[i].z+1)).setBlock(stru[i].state.id, stru[i].state.data).compile())
		}
		let bitmap_stru = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
		let canvas = new android.graphics.Canvas(bitmap_stru);
		for(let i in boxes){
			let block = boxes[i].genTexture(size_block);
			canvas.drawBitmap(block, size - size_block, size - size_block, null);
			block.recycle();
		}
		return bitmap_stru;
	},
	getArrMesh(name, size, value){
		let BaseArr = [];
		let stru = Structure.getStructure(name||[]);
		for(let i in stru){
			let obj = {state: stru[i].state, pos: [stru[i].x, stru[i].y, stru[i].z]}
			if(stru[i].state.id == 0 || value)
				continue;
			let base = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
			base.describeItem({
				id: Block.convertBlockToItemId(stru[i].state.id),
				data: stru[i].state.data,
				size: size || .95,
				material: "visual_structure"
			});
			obj.base = base;
			try{
				if(stru[i].stateExtra.id != 0){
					obj.extra = stru[i].stateExtra;
					let base_extra = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
					base_extra.describeItem({
						id: Block.convertBlockToItemId(stru[i].stateExtra.id),
						data: stru[i].stateExtra.data,
						size: size || .95,
						material: "visual_structure"
					});
					obj.base_extra = base_extra;
				}
			}catch(error){
				
			}
			BaseArr.push(obj)
		}
		return BaseArr;
	},
	Animation(stru, size, value){
		let BaseArr = VisualStructure.getArrMesh(stru, size, value);
		
		this.loaded = false;
		this.getArrBase = function(){
			return BaseArr;
		}
		this.setStructure = function(stru, size, value){
			this.destroy();
			BaseArr = VisualStructure.getArrMesh(stru, size, value);
		}
		this.getStructure = function(){
			return stru;
		}
		let prot = {
			isLoad(){return true},
			load(){return "visual_structure"},
			tick(){},
			tickBlock(){}
		};
		this.setPrototype = function(obj){
			obj.isLoad = obj.isLoad || function(){return true}
			obj.load = obj.load || function(){return "visual_structure"}
			obj.tick = obj.tick || function(){}
			obj.tickBlock = obj.tickBlock || function(){}
			prot = obj;
		}
		this.getPrototype = function(obj){
			return prot;
		}
		this.load = function(x, y, z, a, packet){
			this.loaded = true;
			this.remove = false
			for(let i in BaseArr){
				let pos = BaseArr[i].pos;
				BaseArr[i].base.setPos(x+pos[0],y+pos[1],z+pos[2]);
				if(prot.isLoad(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr[i].base, i, packet))
					BaseArr[i].base.loadCustom(function(){
						prot.tickBlock(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, this, i, packet)
					});
				let material = prot.load(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr[i].base, i, packet)
				BaseArr[i].base.render.setMaterial(material)
				
				BaseArr[i].base.getShaderUniforms().setUniformValue("visual_structure", "A", a || .6);
			}
			this.update = function(){
				prot.tick(x,y,z, packet)
			}
			Updatable.addUpdatable(this)
		}
		this.destroy = function(){
			if(!this.loaded)
				return
			this.loaded = false;
			this.remove = true
			for(let i in BaseArr){
				BaseArr[i].base.destroy();
			}
		}
	}
};
/*
Пусть будет здесь в качестве примера 
Callback.addCallback("StructureLoad", function(){
	//wood_0 структура 
 let Test = new VisualStructure.Animation("wood_0", 1.1);
 Test.setPrototype({
 	load(x, y, z, org_pos, base){
 		return "visual_structure_noy"
 	},
 	tick(x, y, z, packet){
 		if(World.getThreadTime() % 10 == 0){
 			let arr = Test.getArrBase();
 			let value = false;
 			for(let i in arr){
 				if(value)
 					continue
 				let id = BlockSource.getDefaultForActor(Player.get()).getBlockID(x-.5+arr[i].pos[0], y+arr[i].pos[1], z-.5+arr[i].pos[2]);
 				if(arr[i].state.id != id && id != 0){
 					arr[i].base.render.setMaterial("visual_structure_red");
 					value = true;
 				}else if(arr[i].state.id == id){
 					arr[i].base.render.setMaterial("visual_structure_noy");
 				}else{
 					arr[i].base.render.setMaterial("visual_structure");
 				value = true;
 				}
 			}
 		}
 	}
 })
 Callback.addCallback("ItemUseLocal", function(coords, item){
  if(item.id == 264)
   Test.load(coords.x+.5, coords.y+.5, coords.z+.5)
 })
});
*/