#pragma once 

#include "stl/string"

class BlockPos;

struct StructureTemplate {
	BlockPos& getSize() const;
	std::__ndk1::string getName() const;
	bool isLoaded() const;
};