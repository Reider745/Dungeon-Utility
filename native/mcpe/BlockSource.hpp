#pragma once

class BlockPos;
class Block;
class BlockActor;

struct BlockSource {
	void setBlockNoUpdate(BlockPos const&, Block const&);
	BlockActor* getBlockEntity(int, int, int);
};