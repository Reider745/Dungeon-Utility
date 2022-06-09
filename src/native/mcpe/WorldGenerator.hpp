#pragma once 

#include "stl/string"

class Feature;
enum StructureFeatureType {};
class StructureFeature;

struct WorldGenerator {
	StructureFeature* getFeatureName(StructureFeatureType);
	StructureFeatureType getFeatureId(std::__ndk1::string const&);
};