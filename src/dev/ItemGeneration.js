let ItemGenerationJava = WRAP_JAVA("com.reider.dungeonutility.items.ItemGeneration");
let GeneratorJava = WRAP_JAVA("com.reider.dungeonutility.items.Generator");
let ItemGeneration = {
    newGenerator(name){
        ItemGenerationJava.newGenerator(name);
    },
	register(name, generator){
        ItemGenerationJava.register(name, generator instanceof GeneratorJava ? generator : generator.getJava());
    },
    isGenerator(name){
        return ItemGenerationJava.isGenerator(name) == 1;
    },
	setItems(name, items){
		ItemGenerationJava.setItems(name, items);
	},
	getItems(name){
		ItemGenerationJava.getItems(name);
	},
    importFromFile(name, path){
		Callback.invokeCallback("ImportGeneratorFromFile", name, path);
		if(!this.isGenerator(name))
			this.newGenerator(name);
		const loots = FileTools.ReadJSON(path);
		for(let i in loots)
				this.addItem(name, eval(loots[i].id), loots[i].chance, loots[i].count, loots[i].data, loots[i].extra ? (function(){
					let extra = new ItemExtraData();
					extra.setAllCustomData(JSON.stringify(loots[i].extra));
					return extra;
				})() : null);
		this.registerRecipeViewer(name.replace("_", " "), name);
	},
	addItem(name, id, random, count, data, extra){
		count = count || {};
		count.min = count.min || 1;
		count.max = count.max || 2;
		count.slotMax = count.slotMax || 2;
		count.slotMin = count.slotMin || 1;
		ItemGenerationJava.addItem(name, new GeneratorJava.ItemGen(id, data||0, random||1, count.min, count.max, count.slotMin, count.slotMax, extra ? extra : null));
	},
	setItemIntegration(id, random, count, data, extra){
		count = count || {};
		count.min = count.min || 1;
		count.max = count.max || 2;
		count.slotMax = count.slotMax || 2;
		count.slotMin = count.slotMin || 1;
		ItemGenerationJava.setItemIntegration(new GeneratorJava.ItemGen(id, data||0, random||1, count.min, count.max, count.slotMin, count.slotMax, extra ? extra : null));
	},
	setPrototype(name, obj){
		if(!obj.before) obj.before = function(pos, region, packet){}
		if(!obj.after) obj.after = function(pos, region, packet){}
		if(!obj.isGenerate) obj.isGenerate = function(pos, random, slot, item, region, random, packet){return true}
		if(!obj.generate) obj.generate = function(pos, random, slot, item, region, random, packet){}
		ItemGenerationJava.setPrototype(name, obj);
	},
	getPrototype(name){
		return ItemGenerationJava.getPrototype(name);
	},
	fill(name, x, y, z, random, region, packet){
		region = region || BlockSource.getCurrentWorldGenRegion();
		packet = packet || {};
		random = random || new java.util.Random();
		
		ItemGenerationJava.fill(name, x, y, z, random, region, packet);
	},
	registerRecipeViewer(generator, name){
		name = name || "";
		Callback.addCallback('ModsLoaded', function(){
			ModAPI.addAPICallback("RecipeViewer", function(api){
				let arr = ItemGeneration.getItems(generator);
				var RVTypeAW = (function(_super){
					__extends(RVTypeAW, _super);
					function RVTypeAW(nameRv, icon, content){
						let _this = _super.call(this, nameRv, icon, content) || this;
						return _this;
					}
					RVTypeAW.prototype.getAllList = function(){
							let list = [];
							for(let i in arr)
								list.push({
									min: arr[i].getMin(),
									max: arr[i].getMax()-1,
									random: (arr[i].getChance()*100)+"%",
									input: [],
									output: [{id: arr[i].getId(), data: arr[i].getData(), count: 1}]
								});
						return list;
					};
					RVTypeAW.prototype.onOpen = function(elements, data){
						elements.get("textMax").onBindingUpdated("text", "max spawn: "+data.max);
						elements.get("textMin").onBindingUpdated("text", "min spawn: "+data.min);
						elements.get("textChance").onBindingUpdated("text", "chance spawn: "+data.random);
					};
					return RVTypeAW;
				}(api.RecipeType));
				api.RecipeTypeRegistry.register(generator, new RVTypeAW(name, 54, {
					elements: {
						output0: {x: 300, y: 150, size: 120},
						textMax: {type: "text", x: 490, y: 110, font: {size: 40}},
						textMin: {type: "text", x: 490, y: 160, font: {size: 40}},
						textChance: {type: "text", x: 490, y: 210, font: {size: 40}},
					}
				}));
  			});
		});
	},
	enchantAdd(type, count){
		let arr = TYPE[type];
		let extra = new ItemExtraData();
		for(let i=0;i<=count;i++){
			let r = Math.ceil(Math.random()*(arr.length-1));
			let lvl = Math.ceil(Math.random()*(arr[r].l))+1;
			if(arr[r]){
				if(arr[r].e)
					extra.addEnchant(arr[r].e, lvl);
			} 
		}
		return extra;
	}
};