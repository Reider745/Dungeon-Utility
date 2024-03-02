#pragma once

class BlockLegacy;

struct Block {
	unsigned int getRuntimeId() const;
	BlockLegacy* getBlockLegacy() const;
};
