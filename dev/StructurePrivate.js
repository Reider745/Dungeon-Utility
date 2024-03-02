/*let StructurePrivateJava = WRAP_JAVA("com.reider.dungeonutility.struct.StructurePrivate");
let StructurePrivate = {
	addRegion(x1, y1, z1, x2, y2, z2, region){
		alert(x1 + " " + y1 + " " + z1);
		alert(x2 + " " + y2 + " " + z2);
		StructurePrivateJava.addRegion(x1, y1, z1, x2, y2, z2, region);
	},
	addToStructure(stru, x, y, z, region){
		let size = StructureUtility.getStructureSize(stru);
		this.addRegion(size[0].min + x, size[1].min + y, size[2].min + z, size[0].max + x, size[1].max + y, size[2].max + z, region);
	},
	deleteRegion(x, y, z, region){
		StructurePrivateJava.deleteRegion(x, y, z, region);
	},
	isDestoyBlock(x, y, z, region){
		return StructurePrivateJava.isBlockDestroy(x, y, z, region).toString() == "true";
	}
};
Callback.addCallback("DestroyBlock", function(pos, block, player){
	if(StructurePrivate.isDestoyBlock(pos.x, pos.y, pos.z, BlockSource.getDefaultForActor(player)))
		Game.prevent();
});*/