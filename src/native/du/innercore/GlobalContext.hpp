#pragma once 

class Random;
struct Level {
Random* getRandom() const;
};
class ServerLevel : public Level {};
class BlockSource;
class ServerPlayer;

namespace GlobalContext {
	Level* getLevel();
	ServerLevel* getServerLevel();
	BlockSource* getRegion();
	ServerPlayer* getServerPlayer();
}
