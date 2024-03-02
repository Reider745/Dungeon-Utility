#pragma once 

#include "stl/string"

class BlockPos;

struct Vec3 {
	float x, y, z;
	Vec3(BlockPos const&);
	std::__ndk1::string toString() const;
};