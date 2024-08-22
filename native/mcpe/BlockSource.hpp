#pragma once

class BlockPos;
class Block;
class BlockActor;
class Level;

struct BlockSource {
	void setBlockNoUpdate(BlockPos const&, Block const&);
	BlockActor* getBlockEntity(int, int, int);
	Level* getLevel() const;
};