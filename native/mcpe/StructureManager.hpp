#pragma once

#include "stl/string"

class StructureTemplate;

struct StructureManager {
	void clearLoadedStructures();
	StructureTemplate* getStructure(std::__ndk1::string const&) const;
};