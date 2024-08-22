#include "GenerationUtils.hpp"

#include <symbol.h>
#include <hook.h>
#include <logger.h>
#include <innercore/global_context.h>
#include <innercore_callbacks.h>

#include "mcpe/Dimension.hpp"
#include "mcpe/Level.hpp"
#include "Global.hpp"

class ChunkPos {
public:
    static ChunkPos* INVALID;
    int x, z;
};

class LevelChunk {
public:
    ChunkPos& getPosition() const;
    Dimension* getDimension() const;
    Level* getLevel() const;
};

GenerationUtilsModule::GenerationUtilsModule(Module* parent): Module(parent, "dungeon_utility.generation_utils"){}

void GenerationUtilsModule::initialize(){

    /*HookManager::addCallback(
        SYMBOL("mcpe", "_ZNK10LevelChunk37_enableBlockEntityAccessForThisThreadEv"),
        LAMBDA((LevelChunk* self), {
            Logger::debug("Test1", "test 1");
            const Dimension* dimension = self->getDimension();
            if(dimension != nullptr && GlobalContext::getServerLevel() == dimension->getLevel()){
                const ChunkPos& pos = self->getPosition();
                Logger::debug("Test1", "test 2");
                //JavaCallbacks::invokeCallback(DungeonUtility::NativeAPI, "createNewChunk", "(III)V",
                    //pos.x, pos.z, dimension->getDimensionId());
            }
        },),
        HookManager::RETURN | HookManager::LISTENER
    );*/
}