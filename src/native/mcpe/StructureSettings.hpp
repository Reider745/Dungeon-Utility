#pragma once

#include "stl/string"

enum Rotation {};
enum Mirror {};
class Vec3;
enum AnimationMode {};
class ActorUniqueID;
class BlockPos;

struct StructureSettings {
	StructureSettings();
		
	Mirror getMirror() const;
	void setMirror(Mirror);
		
	BlockPos& getStructureSize() const;
	void setStructureSize(BlockPos const&);
		
	bool getIgnoreBlocks() const;
	void setIgnoreBlocks(bool);
		
	bool getIgnoreEntities() const;
	void setIgnoreEntities(bool);
		
	Rotation getRotation() const;
	void setRotation(Rotation);
		
	unsigned int getIntegritySeed() const;
	void setIntegritySeed(unsigned int);
		
	BlockPos& getStructureOffset() const;
	void setStructureOffset(BlockPos const&);
	
	float getIntegrityValue() const;
	void setIntegrityValue(float);
    
	bool getIgnoreJigsawBlocks() const;
	void setIgnoreJigsawBlocks(bool);
	
	std::__ndk1::string& getPaletteName() const;
	void setPaletteName(std::__ndk1::string);
	
	Vec3& getPivot() const;
	void setPivot(Vec3 const&);
    
	bool getReloadActorEquipment() const;
	void setReloadActorEquipment(bool);
    
	ActorUniqueID getLastTouchedByPlayerID() const;
	void setLastTouchedByPlayerID(ActorUniqueID);
    
	void setAnimationTicks(unsigned int);
	unsigned int getAnimationTicks() const;
    
	AnimationMode getAnimationMode() const;
	void setAnimationMode(AnimationMode);
	
	static StructureSettings* DEFAULT_STRUCTURE_OFFSET;
};