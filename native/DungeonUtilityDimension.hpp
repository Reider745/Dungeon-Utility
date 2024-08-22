#ifndef DUNGEON_UTILITY_DIMENSIONS
#define DUNGEON_UTILITY_DIMENSIONS

#include "mcpe/Dimension.hpp"
#include <map>
#include "innercore/DimensionRegistry.hpp"

/*
_ZNK9Dimension9getHeightEv
_ZNK9Dimension12getMinHeightEv
_ZNK11BlockSource13getVoidHeightEv
*/

enum DimensionHeightRange {};
class BlockSource;

namespace DungeonUtilityDimension {
	int getMoonPhase(int dimension){
		Dimension* dim = DimensionRegistry::getDimensionById(dimension);
		if(dim != nullptr)
			return dim->getMoonPhase();
		return -1;
	}
	void init(){
		
	}
};

JS_MODULE_VERSION(N, 1);

JS_EXPORT(N, reg, "I()", (JNIEnv* env){
	int moon = DungeonUtilityDimension::getMoonPhase(0);
	Logger::debug("TEST", "%i", moon);
	return NativeJS::wrapIntegerResult(moon);
});

#endif