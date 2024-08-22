#pragma once

enum Brightness {};
class WorldGenerator;
class Level;

struct Dimension {
	//void getLevelConst() const;
	int getDimensionId() const;
	WorldGenerator getWorldGenerator() const;
	int getMoonPhase() const;
	Level* getLevel() const;
	
	void setSkylight(bool);
	bool hasSkylight() const;
	
	float getSunAngle(float) const;
	Brightness getMoonBrightness() const;
	int getHeightRange() const;
	void setSkyDarken(Brightness);
	
	bool hasCeiling() const;
	void setCeiling(bool);
};