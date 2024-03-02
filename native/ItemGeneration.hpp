#pragma once

#include "mcpe/Random.hpp"
#include "mcpe/LootTableContext.hpp"
#include "mcpe/LootTable.hpp"
#include "mcpe/ItemStack.hpp"

#include "innercore/GlobalContext.hpp"

#include <vector>
#include <map>
#include <regex>
#include <java.h>


struct GenItem {
	int id;
	int data;
	float chance;
	int min;
	int max;
	GenItem(int _id, int _data, float _chance, int _min, int _max){
		min = _min;
		id = _id;
		chance = _chance;
		data = _data;
		max = _max;
	}
};

struct Container {
	int addItem(ItemStack&);
};

class Util {
	public:
		class LootTableUtils {
			public:
				//static void getRandomItems(stl::string&, Random&, LootTableContext&, stl::vector<ItemStack>&);
				static void fillContainer(Level&, Container&, Random&, stl::string const&, Actor*);
		};
};

namespace ItemGeneration {
	std::map<std::string, std::vector<GenItem>> items;
	
	stl::string to_stl(std::string str){
		return stl::string(str.data());
	}
	
	void addItem(std::string name, int id, int data, float chance, int min, int max){
		if(&items[name] == nullptr)
			items[name] = std::vector<GenItem>();
		items[name].push_back(GenItem(id, data, chance, min, max));
	}
	
	void fill(std::string name, int x, int y, int z, BlockSource* region){
		Actor* actor = (Actor*) GlobalContext::getServerPlayer();
		if(actor == nullptr){
			Logger::debug("DungeonUtility", "actor is nullptr");
			return;
		}
		BlockActor* blockActor = region->getBlockEntity(x, y, z);
		if(blockActor == nullptr){
			Logger::debug("DungeonUtility", "blockActor is null");
			return;
		}
		VTABLE_FIND_OFFSET(getContainer, _ZTV10BlockActor, _ZN10BlockActor12getContainerEv);
		Container* container = VTABLE_CALL<Container*>(getContainer, blockActor);
		if(container == nullptr){
			Logger::debug("DungeonUtility", "container is null");
			return;
		}
		Level* level = GlobalContext::getServerLevel();
		Random* random = level->getRandom();
		Util::LootTableUtils::fillContainer(*level, *container, *random, to_stl(name), actor);
	}
	void init(){
		HookManager::addCallback(SYMBOL("mcpe","_ZN9LootTable4fillER9ContainerR6RandomR16LootTableContext"),LAMBDA((HookManager::CallbackController* controller, LootTable* lt, Container& container, Random& random, LootTableContext& ltc),{
			std::string tableName = std::string(lt->getDir().data());		
			std::vector<GenItem> datas = items[tableName];
			if(&datas == nullptr)
				return;
			for(GenItem data : datas){
				if(data.chance > random.nextInt(10000)/10000){
					ItemStack* itemStack = ItemStack::getById(data.id, random.nextInt(data.max - data.min + 1) + data.min, data.data, 0);
					VTABLE_FIND_OFFSET(FillingContainer_setItemOffset, _ZTV16FillingContainer, _ZN16FillingContainer7setItemEiRK9ItemStack);
					VTABLE_CALL<void>(FillingContainer_setItemOffset, &container, random.nextInt(27), itemStack);
					delete itemStack;
				};
			};
		},), HookManager::CONTROLLER );
	}
};
