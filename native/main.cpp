#include <hook.h>
#include <mod.h>
#include <logger.h>
#include <symbol.h>
#include <nativejs.h>
#include "shared_headers/stl/string"

#define stl std::__ndk1

class GuiData {
 public: 
  void displayClientMessage(stl::string const&);
};

class ClientInstance {
 public:
  virtual GuiData* getGuiData();
};

namespace GlobalContext {
	ClientInstance* getMinecraftClient();
};

void message(stl::string text){
	GlobalContext::getMinecraftClient()->getGuiData()->displayClientMessage(text);
}

class Block;
class WallBlock;

namespace BlockRegistry {
	Block* getBlockStateForIdData(int, int);
};

class BlockPos;

class BlockSource {
	public:
		void setBlockNoUpdate(BlockPos const&, Block const&);
};

namespace Reider745 {
	namespace NativeStructure {
		void setBlock(Block* block, BlockSource* region, BlockPos* pos){
			Block* stone = BlockRegistry::getBlockStateForIdData(1, 0);
			region->setBlockNoUpdate(*pos, *stone);
		}
	};
};

class MainModule : public Module {
public:
	MainModule(const char* id): Module(id) {};
	virtual void initialize() {
		DLHandleManager::initializeHandle("libminecraftpe.so", "mcpe");
		HookManager::addCallback(SYMBOL("mcpe", "_ZNK5Block29onStructureNeighborBlockPlaceER11BlockSourceRK8BlockPos"), LAMBDA((Block* self, BlockSource* region, BlockPos* pos),{
			Reider745::NativeStructure::setBlock(self, region, pos);
		},), HookManager::RETURN | HookManager::LISTENER);
		HookManager::addCallback(SYMBOL("mcpe", "_ZNK9WallBlock29onStructureNeighborBlockPlaceER11BlockSourceRK8BlocklockPos"), LAMBDA((WallBlock* self, BlockSource* region, BlockPos* pos),{
			Reider745::NativeStructure::setBlock(self, region, pos);
		},), HookManager::RETURN | HookManager::LISTENER);
  }
};

class OtherModule : public Module {
public:
	OtherModule(Module* parent, const char* id) : Module(parent, id) {};
};

MAIN {
	Module* main_module = new MainModule("sample_library");
	new OtherModule(main_module, "sample_library.other_module");
}