let NativeAPI = WRAP_JAVA("com.reider.dungeonutility.NativeAPI");

Game.isDedicatedServer = Game.isDedicatedServer || function(){
	return false;
};