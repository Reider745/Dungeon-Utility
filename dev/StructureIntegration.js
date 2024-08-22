// TODO: Переписать на ts
let StructureIntegration = {
 registerTreeToBonsaiPots(sapling, stru, obj){
  obj = obj || {};
  obj.move = obj.move || {};
	obj.move.x = obj.move.x || 0;
	obj.move.y = obj.move.y || 0;
	obj.move.z = obj.move.z || 0;
  obj.drops = obj.drops || [];
  ModAPI.addAPICallback("bonsaiTrees", function(api){
   const IdData = api.IdData;
   let _sapling = new IdData(sapling);
   let drops = new api.TreeLootTable(_sapling);
   for(let i in obj.drops){
    let item = obj.drops[i];
    drops.addItem(new IdData(item), item.chance, item.rolls);
   }
   drops.end();
   let tree = new api.TreeMesh(_sapling);
   tree.end();
   Callback.addCallback("LevelDisplayed", function(){
    tree.addMesh(obj.move.x, obj.move.y, obj.move.z, VisualStructure.getRenderMesh(stru)).end();
   });
   api.registerSapling(_sapling, obj.growTime, obj.tags);
  });
 }
};
