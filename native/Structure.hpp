#pragma once 

#include "mcpe/BlockSource.hpp"
#include "mcpe/StructureManager.hpp"
#include "mcpe/StructureTemplate.hpp"
#include "mcpe/StructureSettings.hpp"

#include "mcpe/BlockPos.hpp"
#include "mcpe/Vec3.hpp"
#include "mcpe/ServerPlayer.hpp"
#include "mcpe/ActorUniqueID.hpp"
#include "mcpe/Block.hpp"
#include "mcpe/BlockLegacy.hpp"

#include <innercore/global_context.h>

#define stl std::__ndk1

class BlockPalette {};
struct StructureTelemetryServerData;
class Feature;

namespace Structure {
	inline stl::string to_stl(std::string str){
		return stl::string(str.data());
	}

	StructureManager* manager;
	
	void clearLoadedStructures(){
  		manager->clearLoadedStructures();
	}
	bool isLoad(std::string name){
		if(manager == nullptr)
  			return false;
		StructureTemplate* stru = manager->getStructure(to_stl(name));
		if(stru == nullptr)
			return false;
		return stru->isLoaded();
	}
  	StructureSettings* getDefaultSetting(){
  		StructureSettings* setting = new StructureSettings();
  		setting->setMirror((Mirror) 0);
		setting->setRotation((Rotation) 0);
		setting->setIntegritySeed(0);
		setting->setIntegrityValue((float)100);
		setting->setIgnoreJigsawBlocks(true);
		setting->setIgnoreBlocks(false);
		setting->setIgnoreEntities(true);
		setting->setReloadActorEquipment(false);
		setting->setStructureOffset(BlockPos(0, 0, 0));
		setting->setPaletteName("default");
		setting->setPivot(Vec3(BlockPos(0, 0, 0)));
		setting->setLastTouchedByPlayerID(GlobalContext::getServerPlayer()->getUniqueID());
		setting->setAnimationMode((AnimationMode) 0);
		setting->setAnimationTicks(1);
		return setting;
	}
	void setStructure(std::string name, int x, int y, int z, BlockSource* region){
		if(!isLoad(name))
  			return;
  	
		StructureTemplate* stru = manager->getStructure(to_stl(name));
		StructureSettings* setting = getDefaultSetting();
		Level* level = (Level*) GlobalContext::getServerLevel();

		VTABLE_FIND_OFFSET(Level_getBlockPalette, _ZTV5Level, _ZNK5Level15getBlockPaletteEv);
		BlockPalette& palette = VTABLE_CALL<BlockPalette&>(Level_getBlockPalette, level);

		setting->setStructureSize(stru->getSize());
		
		auto func = (void(*)(StructureTemplate*, BlockSource&, BlockPalette const&, BlockPos const&, StructureSettings const&, StructureTelemetryServerData*, bool)) SYMBOL("mcpe", "_ZNK17StructureTemplate12placeInWorldER11BlockSourceRK12BlockPaletteRK8BlockPosRK17StructureSettingsP28StructureTelemetryServerDatab");
		func(stru, *region, palette, BlockPos(x, y, z), *setting, nullptr, false);
	}
	
	
	void init(){
		HookManager::addCallback(SYMBOL("mcpe", "_ZNK16StructureManager12getStructureERKNSt6__ndk112basic_stringIcNS0_11char_traitsIcEENS0_9allocatorIcEEEE"), LAMBDA((StructureManager* self, stl::string const& name),{
			if(Structure::manager == nullptr)
				Structure::manager = self;
		},), HookManager::CALL | HookManager::LISTENER | HookManager::RESULT);
		
		/*HookManager::addCallback(SYMBOL("mcpe", "_ZNK7Feature11_placeBlockER11BlockSourceRK8BlockPosRK5Block"), LAMBDA((HookManager::CallbackController* controller, Feature* self, BlockSource& region, BlockPos const& pos, Block const& block),{
			int block_id = block.getBlockLegacy()->getBlockItemId();
			JavaCallbacks::invokeControlledCallback(DungeonUtility::NativeAPI, "setBlockFeature", "(IIIIJ)V", controller, 0, pos.x, pos.y, pos.z, block_id, (jlong) &region);
			JNIEnv* env;
			ATTACH_JAVA(env,JNI_VERSION_1_2){
				jmethodID id = env->GetStaticMethodID(DungeonUtility::NativeAPI, "isBlockFeature", "(IIIIJ)Z");
				if(!env->CallStaticBooleanMethod(DungeonUtility::NativeAPI, id, 0, pos.x, pos.y, pos.z, block_id, (jlong) &region))
					controller->replace();
			}
		},), HookManager::CALL | HookManager::LISTENER | HookManager::CONTROLLER);*/
	}
};
