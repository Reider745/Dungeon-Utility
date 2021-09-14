let VisualStructure = {
	getArrMesh(name, size, value){
		let BaseArr = [];
		let pos = [];
		let stru = Structure.getStructure(name||[]);
		for(let i in stru){
			if(stru[i].state.id == 0 || value)
				continue;
			let base = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
			base.describeItem({
				id: stru[i].state.id,
				data: stru[i].state.data,
				size: size || .95,
				material: "visual_structure"
			});
			base.block = {};
			base.block.id = stru[i].state.id;
			BaseArr.push(base);
			pos.push([stru[i].x, stru[i].y, stru[i].z]);
			if(stru[i].stateExtra.id == 0)
				continue;
			let base_extra = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
			base_extra.describeItem({
				id: stru[i].stateExtra.id,
				data: stru[i].stateExtra.data,
				size: size || .95,
				material: "visual_structure"
			});
			BaseArr.push(base_extra);
			pos.push([stru[i].x, stru[i].y, stru[i].z]);
		}
		return {base: BaseArr, pos: pos};
	},
	Animation(stru, size, value){
		let BaseArr = VisualStructure.getArrMesh(stru, size, value);
		
		this.loaded = false;
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
			tick(){}
		};
		this.setPrototype = function(obj){
			obj.isLoad = obj.isLoad || function(){return true}
			obj.load = obj.load || function(){return "visual_structure"}
			obj.tick = obj.tick || function(){}
			prot = obj;
		}
		this.getPrototype = function(obj){
			return prot;
		}
		this.load = function(x, y, z, a){
			this.loaded = true;
			for(let i in BaseArr.base){
				let pos = BaseArr.pos[i];
				BaseArr.base[i].setPos(x+pos[0],y+pos[1],z+pos[2]);
				if(prot.isLoad(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr.base[i]))
					BaseArr.base[i].loadCustom(function(){
						prot.tick(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, this)
					});
				let material = prot.load(x+pos[0],y+pos[1],z+pos[2], {x: pos[0], y: pos[1], z: pos[2]}, BaseArr.base[i])
				BaseArr.base[i].render.setMaterial(material)
				
				BaseArr.base[i].getShaderUniforms().setUniformValue("visual_structure", "A", a || .6);
			}
		}
		this.destroy = function(){
			this.loaded = false;
			for(let i in BaseArr.base){
				BaseArr.base[i].destroy();
			}
		}
	}
};