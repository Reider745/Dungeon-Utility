var __extends = (this && this.__extends) || (function () {

    var extendStatics = function (d, b) {

        extendStatics = Object.setPrototypeOf ||

            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||

            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };

        return extendStatics(d, b);

    };

    return function (d, b) {

        extendStatics(d, b);

        function __() { this.constructor = d; }

        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());

    };

})();
var TYPE = {
  helmet: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 6, l: 3}, {e: 8, l: 1}, {e: 17, l: 3}],
  chestplate: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 17, l: 3}],
  leggings: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, {e: 17, l: 3}],
  boots: [{e: 0, l: 4}, {e: 1, l: 4}, {e: 2, l: 4}, {e: 3, l: 4}, {e: 4, l: 4}, {e: 5, l: 3}, 7, {e: 17, l: 3}],
  sword: [{e: 9, l: 5}, {e: 10, l: 5}, {e: 11, l: 5}, {e: 12, l: 2}, {e: 13, l: 2}, {e: 14, l: 3}, {e: 17, l: 3}],
  shovel: [{e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  pickaxe: [{e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  axe: [{e: 9, l: 5}, {e: 10, l: 5}, {e: 11, l: 5}, {e: 15, l: 5}, {e: 16, l: 1}, {e: 17, l: 3}, {e: 18, l: 3}],
  hoe: [{e: 17, l: 3}],
  bow: [{e: 17, l: 3}, {e: 19, l: 5}, {e: 18, l: 2}, {e: 21, l: 1}, {e: 22, l: 1}],
  fishing: [{e: 17, l: 3}, {e: 23, l: 3}, {e: 24, l: 3}],
  shears: [{e: 15, l: 5}, {e: 17, l: 3}],
};
let generators = {};
let ItemGeneration = {
	newGenerator(name){
		generators[name] = {
			items: [],
			prot: {
				before: function(pos, region, packet){},
				after: function(pos, region, packet){},
				isGenerate: function(pos, random, slot, item, region, packet){return true},
				generate: function(pos, random, slot, item, region, packet){}
			}
		};
	},
	getItems(name){
		return generators[name].items;
	},
	setItems(name, items){
		generators[name].items = items;
	},
	isGenerator(name){
		return !!generators[name];
	},
	importFromFile(name, path){
		Callback.invokeCallback("ImportGeneratorFromFile", name, path);
		if(!this.isGenerator(name))
			this.newGenerator(name);
		const loots = FileTools.ReadJSON(path);
		const items = Object.keys(ItemID);
		const blocks = Object.keys(BlockID);
		for(let i in loots)
			if(loots[i].type == "block")
				this.addItem(name, typeof(loots[i].id) == "number" ? loots[i].id : blocks.indexOf(loots[i].id) != -1 ? BlockID[loots[i].id] : VanillaBlockID[loots[i].id], loots[i].chance, loots[i].count, loots[i].data, loots[i].extra ? (function(){
				let extra = new ItemExtraData();
				extra.setAllCustomData(JSON.stringify(loots[i].extra));
				return extra;
			})() : null);
			else
				this.addItem(name, typeof(loots[i].id) == "number" ? loots[i].id : items.indexOf(loots[i].id) != -1 ? ItemID[loots[i].id] : VanillaItemID[loots[i].id], loots[i].chance, loots[i].count, loots[i].data, loots[i].extra ? (function(){
				let extra = new ItemExtraData();
				extra.setAllCustomData(JSON.stringify(loots[i].extra));
				return extra;
			})() : null);
		this.registerRecipeViewer(name.replace("_", " "), name);
	},
	getAllGenerator(){
		return Object.keys(generators);
	},
	addItem(name, id, random, count, data, extra){
		count = count || {};
		count.min = count.min || 1;
		count.max = count.max+1 || 2;
		count.slotMax = count.slotMax+1 || 2;
		count.slotMin = count.slotMin || 1;
		generators[name].items.push([id || 0, random || 1, count, data || 0, extra || null]);
	},
	setPrototype(name, obj){
		if(!obj.before) obj.before = function(pos, region, packet){}
		if(!obj.after) obj.after = function(pos, region, packet){}
		if(!obj.isGenerate) obj.isGenerate = function(pos, random, slot, item, region, random, packet){return true}
		if(!obj.generate) obj.generate = function(pos, random, slot, item, region, random, packet){}
		generators[name].prot = obj;
	},
	getPrototype(name){
		return generators[name].prot;
	},
	fill(name, x, y, z, random, region, packet){
		region = region || BlockSource.getCurrentWorldGenRegion();
		packet = packet || {};
		random = random || new java.util.Random();
		let container = World.getContainer(x, y, z, region);
		let gen = generators[name];
		if(container){
			gen.prot.before({x: x, y: y, z: z}, region, packet);
			for(let i in gen.items){
				let countSlot = random.nextInt(gen.items[i][2].slotMax-gen.items[i][2].slotMin)+gen.items[i][2].slotMin;
				for(let c = 0;c < countSlot;c++){
					let rand = random.nextFloat();
					if(rand <= gen.items[i][1]){
						let slot = random.nextInt(container.getSize());
						let item = {
							id: gen.items[i][0],
							data: gen.items[i][3],
							extra: gen.items[i][4],
							count: random.nextInt(gen.items[i][2].max-gen.items[i][2].min)+gen.items[i][2].min
						};
						if(gen.prot.isGenerate({x: x, y: y, z: z}, rand, slot, item, region, random,packet))
							container.setSlot(slot, item.id, item.count, item.data, item.extra);
						gen.prot.generate({x: x, y: y, z: z}, rand, slot, item, region, random, packet)
					}
				}
			}
			gen.prot.after({x: x, y: y, z: z}, region, packet)
		}else if(__config__.get("debug.message_error_generation_item")){
			Game.message("noy container x:"+x+", y: "+y+", z: "+z)
		}
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
    				for(let i in arr){
    					list.push({
    						min: arr[i][2].min,
    						max: arr[i][2].max-1,
    						random: (arr[i][1]*100)+"%",
    						input: [],
    						output: [{id: arr[i][0], data: arr[i][3], count: 1}]
    					});
    				}
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
