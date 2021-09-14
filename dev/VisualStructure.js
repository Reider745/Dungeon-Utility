let VisualStructure = {
	getArrMesh(name, size){
		let BaseArr = [];
		let pos = [];
		let stru = Structure.getStructure(name||[]);
		for(let i in stru){
			if(stru[i].state.id == 0)
				continue;
			let base = new Animation.Item(stru[i].x, stru[i].y, stru[i].z);
			base.describeItem({
				id: stru[i].state.id,
				data: stru[i].state.data,
				size: size || .95,
				material: "visual_structure"
			});
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
	Animation(stru, size){
		let BaseArr = VisualStructure.getArrMesh(stru, size);
		
		this.loaded = false;
		this.setStructure = function(stru, size){
			this.destroy();
			BaseArr = VisualStructure.getArrMesh(stru, size);
		}
		this.getStructure = function(){
			return stru;
		}
		this.load = function(x, y, z, a){
			this.loaded = true;
			for(let i in BaseArr.base){
				let pos = BaseArr.pos[i];
				BaseArr.base[i].setPos(x+pos[0],y+pos[1],z+pos[2]);
				BaseArr.base[i].load();
				BaseArr.base[i].getShaderUniforms().setUniformValue("visual_structure", "A", a || .6);
			}
		}
		this.loadCustom = function(x, y, z, func, a){
			this.loaded = true;
			for(let i in BaseArr.base){
				let pos = BaseArr.pos[i];
				BaseArr.base[i].setPos(x+pos[0],y+pos[1],z+pos[2]);
				BaseArr.base[i].loadCustom(func);
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