#pragma once

#include "stl/string"

class BlockPos {
	public:
		int x, y, z;
		BlockPos(int, int, int);
		std::__ndk1::string toString() const;
};